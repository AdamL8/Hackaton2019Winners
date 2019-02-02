# receives and array of url to images and a length --> generates a video with said content
from moviepy.editor import *
from PIL import Image
from io import BytesIO
import requests
import numpy



# testing here
screensize = (1280, 720)

r = requests.get('https://images.radio-canada.ca/v1/ici-premiere/16x9/uneprem-alexandre-taillefer.jpg')
im1 = Image.open(BytesIO(r.content))

r = requests.get('https://images.radio-canada.ca/v1/ici-info/16x9/armes-feu-fusils-registre-hdm.jpg')
im2 = Image.open(BytesIO(r.content))

clip1 = ImageClip(numpy.array(im1)).resize(height=screensize[1]*1.4).resize(lambda t: 1 - 0.01*t).set_duration(7).set_position(lambda t: (0.01*t, 0.01*t), relative=True)
# clip = ImageClip('https://images.radio-canada.ca/v1/ici-premiere/1x1/uneprem-alexandre-taillefer.jpg')
# clip1 = clip1.set_duration(5)
# clip1 = clip1.set_fps(24)

clip2 = ImageClip(numpy.array(im2)).resize(height=screensize[1]*1.2).resize(lambda t: 1 + 0.01*t).set_duration(7).set_position(lambda t: (-0.01*t, -0.01*t), relative=True)
# clip2 = clip2.set_duration(3)
# clip2 = clip2.set_fps(24)
clip1 = CompositeVideoClip([clip1]).resize(width=screensize[0])
clip2 = CompositeVideoClip([clip2]).resize(width=screensize[0])

vid = concatenate_videoclips([clip1, clip2])
vid = CompositeVideoClip([vid.set_position(('center', 'center'))],
                          size=screensize)

vid.write_videofile('test.mp4', fps=24)




def generateVideoFromImages(urls, lengthInSeconds):
    ims = []
    for u in urls:
        r = requests.get(u)
        ims.append(Image.open(BytesIO(r.content)))






