# receives and array of url to images and a length --> generates a video with said content
from moviepy.editor import * # should remove this and import all the necessary functions instead
from moviepy.video.fx import loop, resize
from PIL import Image
from io import BytesIO
import requests
import numpy

# speed = 0.01
# effect_duration = 4
# total_duration = 10

# ------- Resize effects
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


# ------- Media generation
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

def mixAudioAndSubtitles(video, audioPaths, sentences, videoSize):
    audioClips = []
    for p in audioPaths:
        audioClip = AudioFileClip(p)
        audioClip = audioClip.set_duration(audioClip.duration+0.5)
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
    result.set_audio(audios)
    return result

def writeSubtitle(video, subtitle, duration, videoSize):
    # t = TextClip(subtitle, None, (videoSize[0]*0.9, videoSize[1]*0.2), 'transparent', 'white', 'Courier', None, 1, 'caption')
    t = TextClip(subtitle, None, (videoSize[0]*0.9, videoSize[1]*0.2), color='white', bg_color=('transparent'), fontsize=30, method='caption').set_duration(duration)
    # t = t.set_pos('center','bottom')
    result = CompositeVideoClip([video, t.set_pos(('center','bottom'))], size=videoSize)
    return result


    



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

# # generateVideoFromImagesToFile(urls, 15.0, 0.02, (1280,720), 2, 'test_scrapped.mp4')
# vid = generateVideoFromImages(urls, 15.0, 0.02, (1280,720), 2)
# vid = writeSubtitle(vid, 'This is a test. This is a long test. This is a really long test. This is a really really long test. This is a really really really long test.', 8, (1280,720))
# vid.write_videofile('test_subtitle.mp4', fps=24)












