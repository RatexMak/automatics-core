#!/bin/bash

################################################################
# Copyright 2021 Comcast Cable Communications Management, LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
#################################################################

while getopts g:a:v: params
do
    case "${params}" in
        g) groupId=${OPTARG};;
        a) artifactId=${OPTARG};;
        v) version=${OPTARG};;
    esac
done

##Strings to be searched and replaced
findGroupId=com.automatics.apps
findArtifactId=automatics-core
findVersion=2.0.0

echo "Replacing with <groupId>$groupId</groupId><artifactId>$artifactId</artifactId><version>$version</version>"

#Take backup of existing pom.xml
cp pom.xml pom.xml.bk

#Replace tabs with space
sed -i 's/\t/ /g' pom.xml
sed -z -i 's/\n/nnnn/g' pom.xml

#Update artifact id
sed -i "s/$findArtifactId/${artifactId}/g" pom.xml

#Update group id
if [[ ! -z "$groupId" ]]; then
	echo "<dependency>\s*<groupId>$findGroupId"
	sed -i "s/<dependency>nnnn\s*<groupId>$findGroupId/<dependency>\n<groupId>${groupId}/g" pom.xml
fi

#Update version
if [ ! -z "$version" ]; then
	echo "$artifactId</artifactId>nnnn\s*<version>$findVersion"
	sed -i  "s/$artifactId<\/artifactId>nnnn\s*<version>$findVersion/$artifactId<\/artifactId>nnnn<version>$version/g" pom.xml
fi
sed -i 's/nnnn/\n/g' pom.xml

echo "Finished updating pom.xml"





