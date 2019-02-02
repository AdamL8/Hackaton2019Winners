from gensim.summarization.summarizer import summarize
import re

def removehtml(html):
    r = re.compile('<.*?>')
    text = re.sub(r, '', html)
    return text


def convertTextToSummary(text, ratio=0.2):
    return summarize(removehtml(text), ratio)