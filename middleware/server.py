#!/usr/bin/python3
import requests
from flask import Flask, jsonify

CBC_BASE_URL = "https://www.cbc.ca/aggregate_api/v1/"
RADIO_CAN_BASE_URL = "https://services.radio-canada.ca/hackathon/neuro/v1/"

RADIO_CAN_API_KEY = "&client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9"

CBC_ITEMS = CBC_BASE_URL + "items"
RADIO_CAN_NEWS = RADIO_CAN_BASE_URL + "most-popular-content/radcan-news?pageNumber=1"

# r = requests.post('https://httpbin.org/post', data = {'key':'value'})

app = Flask(__name__)

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
    item.categoryName = cbcElement['typeAttributes']['categories'][0]['name']
    item.categoryId =cbcElement['typeAttributes']['categories'][0]['id']
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

@app.route('/api/content/en', methods=['GET'])
def get_tasks_en():
    r = requests.get(CBC_ITEMS)
    return jsonify(list(map(mapCbcItems, r.json())))

@app.route('/api/content/fr', methods=['GET'])
def get_tasks_fr():
    r = requests.get(RADIO_CAN_NEWS + RADIO_CAN_API_KEY)
    return jsonify(list(map(mapRadioCanItems, r.json()["pagedList"]["items"])))

if __name__ == '__main__':
    app.run(debug=True)