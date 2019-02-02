#!/usr/bin/python3
import os
import time
import logging
import threading
import requests
from flask import Flask, jsonify
import schedule

DEBUG_MODE = 'DEBUG' in os.environ

logging.basicConfig()
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG if DEBUG_MODE else logging.WARNING)

PORT = 5000

CBC_BASE_URL = "https://www.cbc.ca/aggregate_api/v1/"
RADIO_CAN_BASE_URL = "https://services.radio-canada.ca/hackathon/neuro/v1/"

RADIO_CAN_API_KEY = "&client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9"

CBC_ITEMS = CBC_BASE_URL + "items?page="
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
    return item.__dict__

def mapRadioCanItems(radioCanElement):
    item = ReturnableItem()
    content = radioCanElement['referredContent']
    item.id = content['id']
    item.title = content['title']
    item.categoryName = content['themeTag']['name']
    item.categoryId = content['themeTag']['id']
    item.url = content['canonicalWebLink']['href']
    item.description = content['outOfContextTitle']
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
        raw_radiocan = requests.get(RADIO_CAN_NEWS + str(i) + RADIO_CAN_API_KEY)
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


@app.route('/api/content/en', methods=['GET'])
def get_tasks_en():
    return jsonify(DATA_CBC)

@app.route('/api/content/fr', methods=['GET'])
def get_tasks_fr():
    return jsonify(DATA_RADIOCAN)

if __name__ == '__main__':
    update_data_thread = threading.Thread(target=data_thread)
    update_data_thread.start()
    app.run(debug=DEBUG_MODE, host='0.0.0.0', port=PORT)
