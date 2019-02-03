# receives and array of url to images and a length --> generates a video with said content
from moviepy.editor import * # should remove this and import all the necessary functions instead
from moviepy.video.fx import loop, resize
from PIL import Image
from io import BytesIO
import requests
import numpy
import glob
import os.path

# speed = 0.01
# effect_duration = 4
# total_duration = 10

# ------- Resize effects
def __zoomInAndOut(t, speed, effect_duration, total_duration):
    if t < effect_duration/2:
        return 1 + speed*t
    elif effect_duration <= t <= effect_duration/2 + (total_duration - effect_duration):
        return 1 + speed*effect_duration
    else:
        return 1 + speed*(total_duration - t)

def __zoomIn(t, speed, effect_duration, total_duration):
    if t < effect_duration:
        return 1 + speed*t
    else:
        return 1 + speed*effect_duration

def __inverseZoomIn(t, speed, effect_duration, total_duration):
    if t < effect_duration:
        return 1 + speed*(effect_duration - t)
    else:
        return 1

# ------- Helpers
def __loop(video, repeats):
    vids = []
    for i in range(0, repeats):
        vids.append(video)
    return concatenate_videoclips(vids)

# ------- Media generation
def _generateVideoFromImages(urls, lengthInSeconds, animSpeed, screensize2d, repeats=0):
    ims = []
    for u in urls:
        r = requests.get(u)
        ims.append(numpy.array(Image.open(BytesIO(r.content))))
    partial_duration = lengthInSeconds/len(ims)/(repeats+1)
    # effect_duration = total_duration/2.5

    clips = []
    i = 0
    for im in ims:
        if len(ims) == 1:
            clips.append(ImageClip(im).resize(width=screensize2d[0]).resize(lambda t: __zoomInAndOut(t, 
                animSpeed, partial_duration, partial_duration)).set_duration(partial_duration))
        elif i % 2 == 0:
            clips.append(ImageClip(im).resize(width=screensize2d[0]).resize(lambda t: __zoomIn(t, 
                animSpeed, partial_duration, partial_duration)).set_duration(partial_duration))
        else:
            clips.append(ImageClip(im).resize(width=screensize2d[0]).resize(lambda t: __inverseZoomIn(t, 
                animSpeed, partial_duration, partial_duration)).set_duration(partial_duration))
        i = i + 1

    vid = concatenate_videoclips(clips)
    if repeats != 0:
        # vid = CompositeVideoClip([vid.set_position(('center', 'center'))],
                                # size=screensize2d).loop(repeats+1)
        vid = __loop(CompositeVideoClip([vid.set_position(('center', 'center'))],
                                size=screensize2d), repeats)
    else:
        vid = CompositeVideoClip([vid.set_position(('center', 'center'))],
                                size=screensize2d)
    return vid

def _mixAudioAndSubtitles(video, audioPaths, sentences, videoSize):
    audioClips = []
    for p in audioPaths:
        audioClip = AudioFileClip(p)
        # audioClip = audioClip.set_duration(audioClip.duration+0.5)
        audioClip = audioClip.set_duration(audioClip.duration)
        audioClips.append(audioClip)
    
    i = 0
    textClips = []
    for a in audioClips:
        t = TextClip(sentences[i], None, (videoSize[0]*0.9, videoSize[1]*0.2), color='white', bg_color=('transparent'), fontsize=30, method='caption')
        t.set_duration(audioClips[i].duration)
        textClips.append(t)
        i = i+1
    
    texts = concatenate_videoclips(textClips)
    audios = concatenate_audioclips(audioClips)
    result = CompositeVideoClip([video, texts.set_pos(('center','bottom'))], size=videoSize)
    result = result.set_audio(audios)
    return result

def _generateAudioAndSubtitles(audioPaths, sentences, videoSize):
    # Does not mix with main video, meaning this gives the audio track on subtitles purely
    audioClips = []
    for p in audioPaths:
        audioClip = AudioFileClip(p)
        # audioClip = audioClip.set_duration(audioClip.duration+0.5)
        audioClip = audioClip.set_duration(audioClip.duration)
        audioClips.append(audioClip)
    
    i = 0
    textClips = []
    for a in audioClips:
        t = TextClip(sentences[i], None, (videoSize[0]*0.9, videoSize[1]*0.2), color='white', bg_color=('transparent'), fontsize=30, method='caption')
        t = t.set_duration(a.duration)
        textClips.append(t)
        i = i+1
    
    texts = concatenate_videoclips(textClips)
    audios = concatenate_audioclips(audioClips)
    texts = texts.set_pos(('center','bottom'))
    # texts = texts.set_audio(audios)
    return texts, audios

def _writeSubtitle(video, subtitle, duration, videoSize):
    t = TextClip(subtitle, None, (videoSize[0]*0.9, videoSize[1]*0.2), color='white', bg_color=('transparent'), fontsize=30, method='caption').set_duration(duration)
    result = CompositeVideoClip([video, t.set_pos(('center','bottom'))], size=videoSize)
    return result

def _generateVideoFromImagesToFile(urls, lengthInSeconds, animSpeed, screensize2d, repeats, outputNamePathAndFormat):
    vid = _generateVideoFromImages(urls, lengthInSeconds, animSpeed, screensize2d, repeats)
    vid.write_videofile(outputNamePathAndFormat, fps=24)
    return outputNamePathAndFormat


## ------- MAIN FUNCTION
def generateVideoAndAudioFromImagesToFile(outputFileNameAndFormat, imageUrls, audioPaths, audioTexts, videoSize=(1280,720)):
    texts, audios = _generateAudioAndSubtitles(audioPaths, audioTexts, videoSize)
    totalLength = texts.duration
    lengthPerImage = totalLength/len(imageUrls)

    repeats = int(lengthPerImage // 5) # Try and keep 5 seconds per image, looping over full list of media if more

    ims = _generateVideoFromImages(imageUrls, totalLength, 0.02, videoSize, repeats)
    result = CompositeVideoClip([ims, texts], size=videoSize)
    result = result.set_audio(audios)
    result.write_videofile(outputFileNameAndFormat, fps=24)
    return result
    # _generateVideoFromImages(imageUrls,)

def getAllMediaUrlsFromFullUrl(neuroUrl):
    r = requests.get(neuroUrl)
    d = r.json()

    urls = []

    # Take care of attached images in body
    if 'body' in d:
        if 'attachments' in d['body']:
            atts = d['body']['attachments']
            for content in atts:
                urls.append(content['concreteImage']['mediaLink']['href'])
    # Take care of images attached in summary
    if 'shareableSummaryMultimediaContent' in d:
        for cIms in d['shareableSummaryMultimediaContent']['concreteImages']:

            if cIms['dimensionRatio'] == '16:9':
                urls.append(cIms['mediaLink']['href'])
    return urls

def getAllMediaUrlsFromContentId(contentId):
    r = requests.get('https://services.radio-canada.ca/hackathon/neuro/v1/news-stories/' + contentId + '?client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9')
    d = r.json()

    urls = []

    # Take care of attached images in body
    if 'body' in d:
        if 'attachments' in d['body']:
            atts = d['body']['attachments']
            for content in atts:
                urls.append(content['concreteImage']['mediaLink']['href'])
    # Take care of images attached in summary
    if 'shareableSummaryMultimediaContent' in d:
        for cIms in d['shareableSummaryMultimediaContent']['concreteImages']:

            if cIms['dimensionRatio'] == '16:9':
                urls.append(cIms['mediaLink']['href'])
    return urls

### TESTING
# CONTENT_ID = '1150596'
# imageUrls = getAllMediaUrlsFromContentId(CONTENT_ID)
# # audioPaths = glob.glob(CONTENT_ID + '/' + CONTENT_ID + '_*.wav')
# # textPaths = glob.glob(CONTENT_ID + '/' + CONTENT_ID + '_*.txt')
# audioPaths = []
# textPaths = []

# i = 0
# filePrefix = CONTENT_ID + '/' + CONTENT_ID + '_'
# while os.path.isfile(filePrefix + str(i) + '.wav'):
#     audioPaths.append(filePrefix + str(i) + '.wav')
#     textPaths.append(filePrefix + str(i) + '.txt')
#     i = i + 1

# audioTexts = []
# i = 0
# for p in textPaths:
#     with open(p, "r") as f:
#         s = f.read()
#         if s == '.':
#             del audioPaths[i]
#             i = i - 1
#         else:
#             audioTexts.append(s)
#         i = i + 1

# generateVideoAndAudioFromImagesToFile("full_test.mp4", imageUrls, audioPaths, audioTexts)



# # r = requests.get('https://services.radio-canada.ca/hackathon/neuro/v1/news-stories/1150640?client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9')
# # d = r.json()

# # urls = []

# # # Take care of attached images in body
# # if 'body' in d:
# #     if 'attachments' in d['body']:
# #         atts = d['body']['attachments']
# #         for content in atts:
# #             urls.append(content['concreteImage']['mediaLink']['href'])
# # # Take care of images attached in summary
# # if 'shareableSummaryMultimediaContent' in d:
# #     for cIms in d['shareableSummaryMultimediaContent']['concreteImages']:

# #         if cIms['dimensionRatio'] == '16:9':
# #             urls.append(cIms['mediaLink']['href'])

# # # generateVideoFromImagesToFile(urls, 15.0, 0.02, (1280,720), 2, 'test_scrapped.mp4')
# # vid = generateVideoFromImages(urls, 15.0, 0.02, (1280,720), 2)
# # vid = writeSubtitle(vid, 'This is a test. This is a long test. This is a really long test. This is a really really long test. This is a really really really long test.', 8, (1280,720))
# # vid.write_videofile('test_subtitle.mp4', fps=24)












