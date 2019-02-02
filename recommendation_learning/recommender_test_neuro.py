import requests
import csv
import os
import json
import time

init_data = []

print(os.path.dirname(os.path.realpath(__file__)))

# Start with RC
with open('dataset/RC/train.csv', newline='') as csvfile:
    csvfile.readline()
    # i = 0
    for row in csvfile:
        # if i == 0:
            # i = i + 1
            # continue
        init_data.append(row.split(","))
        # i = i + 1

# train_data = [['userId', 'contentId', 'contentConsumeOrder', 'subSectorId', 'subSectorName', 'themeTagId', 'themeTagName', 'subThemeTagsIds','subThemeTagsNames', 'regionTagsIds','regionTagsNames']]

subSectorDict = {}
themeTagDict = {}
subthemeTagDict = {}
regionTagDict = {}

# open output file
outFile = open("output.csv", "w")
outWriter = csv.writer(outFile)
outWriter.writerows(['userId', 'contentId', 'contentConsumeOrder', 'subSectorId', 'subSectorName', 'themeTagId', 'themeTagName', 'subThemeTagsIds','subThemeTagsNames', 'regionTagsIds','regionTagsNames'])



for row in init_data:
    userId = row[0]
    contentId = row[1]
    contentConsumeOrder = row[2]
    r = requests.get('https://services.radio-canada.ca/hackathon/neuro/v1/news-stories/'+contentId+'?client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9')
    d = r.json()
    if 'message' in d:
        continue

    # subSector
    subSectorId = d['subSector']['id']
    subSectorName = d['subSector']['name']
    if not subSectorId in subSectorDict:
        subSectorDict[subSectorId] = subSectorName

    # themes
    themeTagId = d['themeTag']['id']
    themeTagName = d['themeTag']['name']
    if not themeTagId in themeTagDict:
        themeTagDict[themeTagId] = themeTagName
    
    # subthemes
    subThemeTagsIds = ''
    subThemeTagsNames = ''
    for sub in d['subThemeTags']:
        subThemeTagsId = sub['id']
        subThemeTagsName = sub['name']
        if not subThemeTagsId in subthemeTagDict:
            subthemeTagDict[subThemeTagsId] = subThemeTagsName
        subThemeTagsIds = subThemeTagsIds + '.' + subThemeTagsId
        subThemeTagsNames = subThemeTagsNames + '.' + subThemeTagsName
    
    # regions
    regionTagsIds = ''
    regionTagsNames = ''
    for sub in d['regionTags']:
        regionTagsId = sub['id']
        regionTagsName = sub['name']
        if not regionTagsId in regionTagDict:
            regionTagDict[regionTagsId] = regionTagsName
        regionTagsIds = regionTagsIds + regionTagsId + '.'
        regionTagsNames = regionTagsNames + regionTagsName + '.'


    
    data_row = [userId, contentId, contentConsumeOrder, subSectorId, subSectorName, themeTagId, themeTagName, subThemeTagsIds, subThemeTagsNames, regionTagsIds,  regionTagsNames]
    # train_data.append(data_row)
    print('Working on user: ' + userId + ' for contentId: ' + contentId + ' in order: ' + contentConsumeOrder)
    outWriter.writerows(data_row)
    time.sleep(0.1)


subSectorDict = {'subSectorDict': subSectorDict}
themeTagDict = {'themeTagDict': themeTagDict}
subthemeTagDict = {'subthemeTagDict': subthemeTagDict}
regionTagDict = {'regionTagDict': regionTagDict}

# Saving dicts
with open('dicts.txt', 'w') as dictsFile:
    dictsFile.write(json.dumps(subSectorDict))
    dictsFile.write(json.dumps(themeTagDict))
    dictsFile.write(json.dumps(subthemeTagDict))
    dictsFile.write(json.dumps(regionTagDict))



