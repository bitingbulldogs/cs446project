#!/usr/bin/python
import json

'''
example of an item entry in data.json:
{ "pantry": "3-4 days",
  "freezer": "1-2 months",
  "refrigerator": "2-3 months",
  "name": "onions, all varieties except sweet (including yellow, white, pearl) - fresh, raw, whole"}
'''

def getDaysAsInt(expiry):
  if expiry.find('-') != -1: # ex. '3-4' returns 3
    return int(expiry[:expiry.find('-')])
  else: # ex. '14' returns 14
    return int(expiry)

# convert from days/weeks/months/etc. to days
def formatExpiry(expiry):
  if expiry == '':
    return -1
  elif expiry.find('day') != -1: # ex. '3-4 days' returns 3 or '14 days' returns 14
    return getDaysAsInt(expiry[:expiry.find(' ')])
  elif expiry.find('week') != -1: # ex. '2-3 weeks' returns 14
    return getDaysAsInt(expiry[:expiry.find(' ')]) * 7
  elif expiry.find('month') != -1: # ex. '2-3 months' returns 60
    return getDaysAsInt(expiry[:expiry.find(' ')]) * 30
  elif expiry.find('year') != -1: # ex. '2 years' returns 365*2
    return getDaysAsInt(expiry[:expiry.find(' ')]) * 365
  elif expiry.find('keeps indefinitely') != -1:
    return 99999
  else:
    return -1

def getFoodExpiry(item):
  pantry = -1
  refrigerator = -1
  freezer = -1

  pantry = formatExpiry(item['pantry'])
  if pantry != -1:
    return pantry
  refrigerator = formatExpiry(item['refrigerator'])
  if refrigerator != -1:
    return refrigerator
  freezer = formatExpiry(item['freezer'])
  if freezer != -1:
    return freezer

def getFoodName(item):
  if item['name'].find('cooked') != -1 and item['name'].find('uncooked') == -1:
    # ex. 'fish - cooked' and 'fish - uncooked', we only care about the uncooked one
    return -1
  if item['name'].find('opened') != -1 and item['name'].find('unopened') == -1:
    return -1
  if item['name'].find('frozen') != -1:
    return -1
  hyphen = item['name'].find('-')
  if hyphen != -1:
    item['name'] = item['name'][:hyphen-1]
  comma = item['name'].find(',')
  if comma != -1:
    item['name'] = item['name'][:comma]

  return item['name']


# MAIN
data = {}
parsedExpiry = {} # ex. {'oranges' : 5, 'apples' : 7}

# load data from data.json (scraped from StillTasty)
jsonData = open('data.json')
data = json.load(jsonData)['data']
jsonData.close()

for item in data:
  name = getFoodName(item)
  if name == -1:
   continue

  expiry = getFoodExpiry(item)
  if expiry == -1:
    continue
  if name not in parsedExpiry:
    parsedExpiry[name] = expiry
  else:
    continue

# store parsedExpiry in parsed.json
file = open('parsed.json', 'w')
file.write(json.dumps(parsedExpiry))

