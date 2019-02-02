import os, requests, time
from xml.etree import ElementTree

SUBSCRIPTION_KEY = "1de26d9ced0748359619bce77c24008d"
BASE_URL = "https://eastus.tts.speech.microsoft.com/"
PATH = "cognitiveservices/v1"

def tts(text, lang="en"):
    fetch_token_url = "https://eastus.api.cognitive.microsoft.com/sts/v1.0/issueToken"
    response = requests.post(fetch_token_url, headers={'Ocp-Apim-Subscription-Key': SUBSCRIPTION_KEY})
    access_token = str(response.text)

    constructed_url = BASE_URL + PATH
    headers = {
        'Authorization': 'Bearer ' + access_token,
        'Content-Type': 'application/ssml+xml',
        'X-Microsoft-OutputFormat': 'riff-24khz-16bit-mono-pcm'
    }
    # Todo switch based of lang
    xml_body = ElementTree.Element('speak', version='1.0')
    xml_body.set('{http://www.w3.org/XML/1998/namespace}lang', 'en-us')
    voice = ElementTree.SubElement(xml_body, 'voice')
    voice.set('{http://www.w3.org/XML/1998/namespace}lang', 'en-US')
    voice.set('name', 'Microsoft Server Speech Text to Speech Voice (en-US, Guy24KRUS)')
    voice.text = text
    body = ElementTree.tostring(xml_body)

    response = requests.post(constructed_url, headers=headers, data=body)
    if response.status_code == 200:
        return response.content
    else:
        print("\nStatus code: " + str(response.status_code) + "\nSomething went wrong. Check your subscription key and headers.\n")
        return None
