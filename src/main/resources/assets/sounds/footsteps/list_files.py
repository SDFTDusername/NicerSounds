import os
import json

result = {}
for path in os.listdir("./"):
    if not os.path.isdir(path):
        continue
    result[path] = os.listdir(path)

with open("contents.json", "w") as file:
    json.dump(result, file, indent='\t')

print("Done! Saved to contents.json")