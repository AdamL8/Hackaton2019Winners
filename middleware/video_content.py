# receives and array of url to images and a length --> generates a video with said content
from moviepy.editor import *
from moviepy.video.fx import loop, resize
from PIL import Image
from io import BytesIO
import requests
import numpy

# speed = 0.01
# effect_duration = 4
# total_duration = 10

def zoomInAndOut(t, speed, effect_duration, total_duration):
    if t < effect_duration:
        return 1 + speed*t
    elif effect_duration <= t <= effect_duration*3/2:
        return 1 + speed*effect_duration
    else:
        return 1 + speed*(total_duration - t)

def zoomIn(t, speed, effect_duration, total_duration):
    if t < effect_duration:
        return 1 + speed*t
    else:
        return 1 + speed*effect_duration

def inverseZoomIn(t, speed, effect_duration, total_duration):
    if t < effect_duration:
        return 1 + speed*(effect_duration - t)
    else:
        return 1

def generateVideoFromImages(urls, lengthInSeconds, animSpeed, screensize2d, repeats=0):
    ims = []
    for u in urls:
        r = requests.get(u)
        ims.append(numpy.array(Image.open(BytesIO(r.content))))
    partial_duration = lengthInSeconds/len(ims)/(repeats+1)
    # effect_duration = total_duration/2.5

    clips = []
    i = 0
    for im in ims:
        # clips.append(ImageClip(im).resize(width=screensize2d[0]).resize(lambda t: zoomInAndOut(t,
        if i % 2 == 0:
            clips.append(ImageClip(im).resize(width=screensize2d[0]).resize(lambda t: zoomIn(t, 
                animSpeed, partial_duration, partial_duration)).set_duration(partial_duration))
        else:
            clips.append(ImageClip(im).resize(width=screensize2d[0]).resize(lambda t: inverseZoomIn(t, 
                animSpeed, partial_duration, partial_duration)).set_duration(partial_duration))
        i = i + 1
        # clips.append(ImageClip(im).fx(resize, zoomInAndOut, method='bilinear').set_duration(total_duration))

    vid = concatenate_videoclips(clips)
    if repeats != 0:
        vid = CompositeVideoClip([vid.set_position(('center', 'center'))],
                                size=screensize2d).loop(repeats+1)
    else:
        vid = CompositeVideoClip([vid.set_position(('center', 'center'))],
                                size=screensize2d)
    return vid

def generateVideoFromImagesToFile(urls, lengthInSeconds, animSpeed, screensize2d, repeats, outputNamePathAndFormat):
    vid = generateVideoFromImages(urls, lengthInSeconds, animSpeed, screensize2d, repeats)
    vid.write_videofile(outputNamePathAndFormat, fps=24)
    return outputNamePathAndFormat


### TESTING
# r = requests.get('https://services.radio-canada.ca/hackathon/neuro/v1/news-stories/1150640?client_key=31b2bb0e-85ec-4406-9b22-31c93d7e75f9')
# d = r.json()

# urls = []

# # Take care of attached images in body
# if 'body' in d:
#     if 'attachments' in d['body']:
#         atts = d['body']['attachments']
#         for content in atts:
#             urls.append(content['concreteImage']['mediaLink']['href'])
# # Take care of images attached in summary
# if 'shareableSummaryMultimediaContent' in d:
#     for cIms in d['shareableSummaryMultimediaContent']['concreteImages']:

#         if cIms['dimensionRatio'] == '16:9':
#             urls.append(cIms['mediaLink']['href'])

# generateVideoFromImagesToFile(urls, 15.0, 0.02, (1280,720), 2, 'test_scrapped.mp4')
        












