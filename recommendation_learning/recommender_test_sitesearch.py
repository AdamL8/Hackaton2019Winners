import requests
import csv
import os
import json
import time

# constants
IDPACK_SIZE = 50
SS_API_GLOBALIDS = 'https://services.radio-canada.ca/hackathon/sitesearch/v1/internal/rcgraph/indexable-content-summaries?client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9&GlobalIds='

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

# open output file
outFile = open("output_sitesearch.csv", "w")
outWriter = csv.writer(outFile)
outWriter.writerow(['userId', 'contentId', 'contentConsumeOrder', 'subSectorId', 'subSectorName', 'themeTagId', 'subThemeTagsIds', 'regionTagsIds'])


numberOfRows = len(init_data)
numberOfPacks = numberOfRows//IDPACK_SIZE

for i in range(0,numberOfPacks):
    queryGlobalIdsString = ''
    for j in range(i*IDPACK_SIZE, min((i+1)*IDPACK_SIZE, numberOfRows)):
        queryGlobalIdsString = queryGlobalIdsString + "11-" + init_data[j][1] + ","
    
    queryGlobalIdsString = queryGlobalIdsString[:-1]

    completed = False
    r = []
    d = []
    while not completed:
        r = requests.get(SS_API_GLOBALIDS + queryGlobalIdsString)
        d = r.json()
        if 'queryParameters' in d:
            completed = True
        else:
            time.sleep(0.5)
    

    js = range(i*IDPACK_SIZE, min((i+1)*IDPACK_SIZE, numberOfRows))
    j = 0
    for item in d['items']:
        userId = init_data[js[j]][0]
        contentId = init_data[js[j]][1]
        contentConsumeOrder = init_data[js[j]][2][:-1]

        # subSector
        if item['subSector']:
            subSectorId = item['subSector']['id']
            subSectorName = item['subSector']['name']
        else:
            subSectorId = ''
            subSectorName = ''

        # themes
        themeTagId = item['themeId']
        
        # subthemes
        subThemeTagsIds = item['subThemeIds']
        
        # regions
        regionTagsIds = item['regionIds']

        # add to db
        data_row = [userId, contentId, contentConsumeOrder, subSectorId, subSectorName, themeTagId, subThemeTagsIds, regionTagsIds]
        print('Working on user: ' + userId + ' for contentId: ' + contentId + ' in order: ' + contentConsumeOrder)
        outWriter.writerow(data_row)
        # time.sleep(0.2)



        j = j + 1


