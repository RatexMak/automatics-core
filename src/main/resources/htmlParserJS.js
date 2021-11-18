/*
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
function myFunction(){
var acc = document.getElementsByClassName("accordion");
var i;
console.log("accordion-"+acc.length)
for (i = 0; i < acc.length; i++) {
  acc[i].onclick = function() {
    this.classList.toggle("active");
    var panel = this.nextElementSibling;
    //console.log("hehe"+panel.nextElementSibling)
    var lastchild = panel.lastElementChild;
    //var firstchild = this.firstElementChild;   //for displaying the first line
    if (panel.style.maxHeight){
      panel.style.maxHeight = null;
      //firstchild.style.display="block";
      panel.style.borderBottomStyle  ="hidden";
      console.log(panel.style.borderBottomStyle);
    } else {
      panel.style.maxHeight = lastchild.scrollHeight + panel.scrollHeight + "px";
      //firstchild.style.display="none";
      panel.style.borderBottomStyle  ="ridge";
      console.log(panel.style.borderBottomStyle);
   }
  }
}
}