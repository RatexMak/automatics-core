#!/bin/bash
​
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
​
while getopts g:a:v: params
do
    case "${params}" in
        g) groupId=${OPTARG};;
        a) artifactId=${OPTARG};;
        v) version=${OPTARG};;
    esac
done
​
##Strings to be searched and replaced
stringToReplace="<dependencies>"
stringToReplacePlugin="<\/plugins>"
​
echo "Adding dependency <groupId>$groupId</groupId><artifactId>$artifactId</artifactId><version>$version</version>"
​
#Take backup of existing pom.xml
cp pom.xml pom.xml.bk
​
#Replace tabs with space
sed -i 's/\t/ /g' pom.xml
sed -z -i 's/\n/nnnn/g' pom.xml
​
#Add partner dependency
sed -i "s/$stringToReplace/<dependencies><dependency><groupId>$groupId<\/groupId><artifactId>$artifactId<\/artifactId><version>$version<\/version><\/dependency>/g" pom.xml
​
#Add plugin
sed -i "s/$stringToReplacePlugin/<plugin>\n\t\t<groupId>org.apache.maven.plugins<\/groupId>\n\t\t<artifactId>maven-shade-plugin<\/artifactId>\n\t\t<version>3.1.1<\/version>\n\t\t<configuration>\n\t\t\t<createDependencyReducedPom>false<\/createDependencyReducedPom>\n\t\t<\/configuration>\n\t\t<executions>\n\t\t\t<execution>\n\t\t\t\t<phase>package<\/phase>\n\t\t\t\t<goals>\n\t\t\t\t\t<goal>shade<\/goal>\n\t\t\t\t<\/goals>\n\t\t\t\t<configuration>\n\t\t\t\t\t<transformers>\n\t\t\t\t\t\t<transformer\n\t\t\t\t\t\t\timplementation=\"org.apache.maven.plugins.shade.resource.ManifestResourceTransformer\">\n\t\t\t\t\t\t\t<mainClass>com.automatics.executor.Starter<\/mainClass>\n\t\t\t\t\t\t<\/transformer>\n\t\t\t\t\t\t<transformer implementation=\"org.apache.maven.plugins.shade.resource.ServicesResourceTransformer\" \/>\n\t\t\t\t\t<\/transformers>\n\t\t\t\t<\/configuration>\n\t\t\t<\/execution>\n\t\t<\/executions>\n\t<\/plugin>\n&/g" pom.xml
​
sed -i 's/nnnn/\n/g' pom.xml
​
echo "Finished updating pom.xml"