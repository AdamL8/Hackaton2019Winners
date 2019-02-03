import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

df = pd.read_csv('./user-theme.csv', header=None, usecols=[0, 1])

# format user ids
df[0] = df[0].map(lambda s : int(s.replace("user-", "")))
df[1] = df[1].map(lambda s : int(s))

num_categories = 25

groups = np.array([grp.values[:,1] for _, grp in df.groupby([0])])
npgroups = np.zeros((len(groups), num_categories))

def transform_user(array_of_themes):
    v = np.zeros(num_categories)
    for j in array_of_themes:
        v[j] += 1
    return v

for i in range(len(groups)):
    npgroups[i,:] = transform_user(groups[i])


# Array of liked / listened themes (ex: [20, 19, 16, 18]) 
# return best next theme to listen to
def get_closest(liked_themes):
    one_hotted = transform_user(liked_themes)
    s = np.vstack([one_hotted, npgroups])
    simili = cosine_similarity(s, s)
    closest = npgroups[np.argmax(simili[0][np.nonzero(simili[0]-1)])-1]
    return np.argmax(closest - one_hotted)
