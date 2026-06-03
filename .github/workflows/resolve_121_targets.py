#!/usr/bin/env python3
import json
import re
import urllib.request
import xml.etree.ElementTree as ET


def read(url: str) -> bytes:
    with urllib.request.urlopen(url, timeout=30) as response:
        return response.read()


games = json.loads(read("https://meta.fabricmc.net/v2/versions/game"))
stable_121 = [
    game["version"]
    for game in games
    if game.get("stable") and re.fullmatch(r"1\.21(?:\.\d+)?", game["version"])
]

yarn_xml = ET.fromstring(read("https://maven.fabricmc.net/net/fabricmc/yarn/maven-metadata.xml"))
yarn_versions = [node.text for node in yarn_xml.findall("./versioning/versions/version")]

for minecraft_version in reversed(stable_121):
    prefix = f"{minecraft_version}+build."
    matching_yarn = [version for version in yarn_versions if version.startswith(prefix)]
    if not matching_yarn:
        raise SystemExit(f"No Yarn mappings found for Minecraft {minecraft_version}")
    print(minecraft_version, matching_yarn[-1])
