#!/usr/bin/python3
import os
import time
import logging
import threading
import requests
from flask import Flask, jsonify, make_response, send_from_directory
import schedule
import html2text
import re
from tts import tts
from textToSentence import split_into_sentences
import wave
import uuid
from video_content import generateVideoAndAudioFromImagesToFile, getAllMediaUrlsFromContentId
import os.path
from summarizer import convertTextToSummary 

DEBUG_MODE = 'DEBUG' in os.environ

logging.basicConfig()
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG if DEBUG_MODE else logging.WARNING)

PORT = 5000 if DEBUG_MODE else 80

CBC_BASE_URL = "https://www.cbc.ca/aggregate_api/v1/"
RADIO_CAN_BASE_URL = "https://services.radio-canada.ca/hackathon/neuro/v1/"

RADIO_CAN_API_KEY = "client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9"

CBC_ITEMS = CBC_BASE_URL + "items/?type=story&page="
RADIO_CAN_NEWS = RADIO_CAN_BASE_URL + "most-popular-content/radcan-news?pageNumber="

app = Flask(__name__)
# Hold the data in ram to avoid the slow latency of their apis
DATA_CBC = []
DATA_RADIOCAN = []

class ReturnableItem(object):
    def __init__(self, **kwargs):
        self.id = None
        self.categoryName = None
        self.categoryId = None
        self.title = None
        self.url = None
        self.description = None
        self.image = None
        self.__dict__.update(kwargs)

def mapCbcItems(cbcElement):
    item = ReturnableItem()
    item.id = cbcElement['id']
    item.title = cbcElement['title']
    if len(cbcElement['typeAttributes']['categories']) > 0:
        # TODO: take account the other categories
        item.categoryName = cbcElement['typeAttributes']['categories'][0]['name']
        item.categoryId = cbcElement['typeAttributes']['categories'][0]['id']
    item.url = cbcElement['typeAttributes']['url']
    item.description = cbcElement['description']
    #item.sourceId = cbcElement['sourceId']
    for key, value in cbcElement['images'].items():
        if "square" in key:
            item.image = value
            break
    return item.__dict__

def mapRadioCanItems(radioCanElement):
    item = ReturnableItem()
    content = radioCanElement['referredContent']
    item.id = content['id']
    item.title = content['title'].replace("&nbsp;", " ")
    item.categoryName = content['themeTag']['name']
    item.categoryId = content['themeTag']['id']
    item.url = content['canonicalWebLink']['href']
    item.description = content['outOfContextTitle'].replace("&nbsp;", " ") #change to summary if html->text works
    if 'summaryMultimediaItem' in content and 'concreteImages' in content['summaryMultimediaItem']:
        for i in content['summaryMultimediaItem']['concreteImages']:
            if i['dimensionRatio'] == "1:1":
                item.image = i['mediaLink']['href']
                break
    return item.__dict__

def update_data():
    global DATA_CBC
    global DATA_RADIOCAN
    logger.info("Starting the update")
    temp_data_radiocan = []
    temp_data_cbc = []
    for i in range(1, 5):
        raw_cbc = requests.get(CBC_ITEMS + str(i))
        temp_data_cbc += list(map(mapCbcItems, raw_cbc.json()))
        raw_radiocan = requests.get(RADIO_CAN_NEWS + str(i) + "&" + RADIO_CAN_API_KEY)
        radiocan_json = raw_radiocan.json()
        if "pagedList" in radiocan_json and "items" in radiocan_json["pagedList"]:
            temp_data_radiocan += list(map(mapRadioCanItems, raw_radiocan.json()["pagedList"]["items"]))
        time.sleep(1)
    DATA_RADIOCAN = temp_data_radiocan
    DATA_CBC = temp_data_cbc
    logger.info("Data update done")

def data_thread():
    update_data()
    schedule.every(1).minutes.do(update_data)
    while True:
        schedule.run_pending()
        time.sleep(6)


def cbc_parse_content_recur(body):
    txt = ""
    for i in body['content']:
        if i['type'] == "text":
            txt += i['content'] + " "
        elif i['type'] == "html":
            txt += cbc_parse_content_recur(i)
    return txt

def get_cbc_content(id):
    URL = CBC_BASE_URL + "/items/" + id + "?inline=sourceDetails"
    raw_data = requests.get(URL).json()
    content = ""
    if "body" in raw_data['typeAttributes']:
        body = raw_data['typeAttributes']['body']
        content = cbc_parse_content_recur(body)
        content = str(content).replace(u"\u00A0", " ")
    return {"content": content}

def get_radiocan_content(id):
    URL = RADIO_CAN_BASE_URL + "/news-stories/" + id + "?" + RADIO_CAN_API_KEY 
    raw_data = requests.get(URL).json()
    content = ""
    if "body" in raw_data:
        h = html2text.HTML2Text()
        h.ignore_links = True
        h.strong_mark = ""
        h.emphasis_mark = ""
        content = re.sub(r'<.*?>', '', raw_data["body"]["html"].replace(">", "> ")).replace("&nbsp;", " ").replace("\n", "").replace("  ", " ")
    return {"content": content}

def get_sentences(content):
    return split_into_sentences(content)

@app.route('/api/content/en', methods=['GET'])
def get_tasks_en():
    return jsonify(DATA_CBC)

@app.route('/api/content/fr', methods=['GET'])
def get_tasks_fr():
    return jsonify(DATA_RADIOCAN)

@app.route('/api/content/en/<id>', methods=['GET'])
def get_content_en(id):
    return jsonify(get_cbc_content(id))

@app.route('/api/content/fr/<id>', methods=['GET'])
def get_content_fr(id):
    return jsonify(get_radiocan_content(id))

@app.route('/api/content/summary/en/<id>', methods=['GET'])
def get_summary_content_en(id):
    return jsonify(convertTextToSummary(get_cbc_content(id)['content']))

@app.route('/api/content/summary/fr/<id>', methods=['GET'])
def get_summary_content_fr(id):
    return jsonify(convertTextToSummary(get_radiocan_content(id)['content']))

@app.route('/api/content/summary/audio/en/<id>', methods=['GET'])
def get_summary_content_audio_en(id):
    response = make_response(tts(convertTextToSummary(convertTextToSummary(get_cbc_content(id)['content'])), "en"))
    response.headers['Content-Type'] = 'audio/wav'
    return response

@app.route('/api/content/summary/audio/fr/<id>', methods=['GET'])
def get_summary_content_audio_fr(id):
    response = make_response(tts(convertTextToSummary(convertTextToSummary(get_radiocan_content(id)['content'])), "fr"))
    response.headers['Content-Type'] = 'audio/wav'
    return response

# TTS routes
@app.route('/api/audio/en/<id>')
def get_audio_en(id):
    response = make_response(tts(get_cbc_content(id)["content"], "en"))
    response.headers['Content-Type'] = 'audio/wav'
    return response

@app.route('/api/audio/fr/<id>')
def get_audio_fr(id):
    response = make_response(tts(get_radiocan_content(id)["content"], "en"))
    response.headers['Content-Type'] = 'audio/wav'
    return response

@app.route('/api/tts/en/<id>')
def get_tts_en(id):
    sentences = get_sentences(get_cbc_content(id)["content"])
    response = writeWave(id, sentences, "en")

    return jsonify(response)

@app.route('/api/tts/fr/<id>')
def get_tts_fr(id):
    sentences = get_sentences(get_radiocan_content(id)["content"])
    response = writeWave(id, sentences, "fr")

    return jsonify(response)

def writeWave(id, sentences, lang):
    response = []

    if not os.path.isdir(id):
        os.makedirs(id)

    index = 0
    for sentence in sentences:
        waveName = id + '_' + str(index)+ '.wav'
        wavePath = id + '/' + waveName
        with open(wavePath, 'wb') as audio:
            audio.write(tts(sentence, lang))

        subtitleName = id + '_' + str(index) + '.txt'
        subtitlePath = id + '/' + subtitleName
        with open(subtitlePath, "w") as subtitle:
            subtitle.write(sentence)

        response.append({"sentenceId": index, "newsId": id, "dirPath": os.getcwd() + '/' + id, "wave": waveName, "text": sentence})
        index += 1

    return response

def writeWaveArrays(id, sentences, lang):
    # response = []
    audioPaths = []
    audioTexts = []

    if not os.path.isdir(id):
        os.makedirs(id)

    index = 0
    for sentence in sentences:
        waveName = id + '_' + str(index)+ '.wav'
        wavePath = id + '/' + waveName
        with open(wavePath, 'wb') as audio:
            audio.write(tts(sentence, lang))
            audioPaths.append(wavePath)

        # subtitleName = id + '_' + str(index) + '.txt'
        # subtitlePath = dirPath + '/' + subtitleName
        # with open(subtitlePath, "w") as subtitle:
        #     subtitle.write(sentence)
        audioTexts.append(sentence)

        # response.append({"sentenceId":index, "newsId": id, "dirPath": os.getcwd() + '/' + dirPath, "wave": waveName, "text": sentence})
        index += 1

    return audioPaths, audioTexts


# wav and text download
@app.route('/audio_dump/<path:path>')
def send_audio(path):
    splittedPath = path.split('/')
    folderPath = '/'

    i = 0
    while i < len(splittedPath) -1:
        folderPath += splittedPath[i] + '/'
        i += 1

    fileName = splittedPath[len(splittedPath) -1]
    
    return send_from_directory(folderPath, fileName)

# video creation
@app.route('/api/videotts/fr/<id>', defaults={'height':720})
@app.route('/api/videotts/fr/<id>/<height>')
def videotts_fr(id, height):
    width = int(height*16/9)
    if not os.path.isfile(id + '/' + id + '.webm'):
        videoSize = (width, height)

        sentences = get_sentences(get_radiocan_content(id)["content"])
        audioPaths, audioTexts = writeWaveArrays(id, sentences, "fr")
        imageUrls = getAllMediaUrlsFromContentId(id)
        generateVideoAndAudioFromImagesToFile(id + '/' + id + '.webm', imageUrls, audioPaths, audioTexts, videoSize)
    
    return jsonify({'videoPath': os.getcwd() + '/' + id + '/' + id + '.webm', 'width': width, 'height': height})

############################################################################################
# MARK: To enable that next route, new functions have to be created to scrape the CBC API,
# that means (in video_content.py): getAllMediaUrlsFromContentId(contentId)
# the rest should work well with whatever language, audio and images (although please use
# 16:9 images, else it's undefined behaviorTM)
############################################################################################

# @app.route('/api/videotts/en/<id>', defaults={'height':720})
# @app.route('/api/videotts/en/<id>/<height>')
# def videotts_en(id, height):
#     width = int(height*16/2)
#     if not os.path.isfile(id + '/' + id + '.webm'):
#         videoSize = (width, height)

#         sentences = get_sentences(get_radiocan_content(id)["content"])
#         audioPaths, audioTexts = writeWaveArrays(id, sentences, "en")
#         imageUrls = getAllMediaUrlsFromContentId(id)
#         generateVideoAndAudioFromImagesToFile(id + '/' + id + '.webm', imageUrls, audioPaths, audioTexts, videoSize)
    
#     return jsonify({'videoPath': id + '/' + id + '.webm', 'width': width, 'height': height})

############################################################################################
# We should probably implement the summarize functionalities too
############################################################################################


############################################################################################
# This is the function for generating videos before requests (the english one is to do)
############################################################################################
def createVideoTTSForCaching_fr(id, height=720):
    width = int(height*16/9)
    if not os.path.isfile(id + '/' + id + '.webm'):
        videoSize = (width, height)

        sentences = get_sentences(get_radiocan_content(id)["content"])
        audioPaths, audioTexts = writeWaveArrays(id, sentences, "fr")
        imageUrls = getAllMediaUrlsFromContentId(id)
        generateVideoAndAudioFromImagesToFile(id + '/' + id + '.webm', imageUrls, audioPaths, audioTexts, videoSize)




if __name__ == '__main__':
    print ('Debug mode is: %r' % (DEBUG_MODE) )
    update_data_thread = threading.Thread(target=data_thread)
    update_data_thread.start()
    app.run(debug=DEBUG_MODE, host='0.0.0.0', port=PORT)


