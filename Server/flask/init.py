# coding=utf-8
import json
import requests

from flask import Flask, request, jsonify
from flask_cors import CORS
from konlpy.tag import Mecab

app = Flask (__name__)
CORS(app)

# json 에서 필요한 값 찾는 함수
def find_by_html(html, target, list):

    index = -1
    while True:
        index = html.find(target, index + 1)
        if index == -1:
            break

        tempHtml = html[index + len(target):]
        quotIndex = tempHtml.find('"')
        tempStr = html[index + len(target): quotIndex + index + len(target)]
        list.append(tempStr)

# 부산대 오탈자 체크
def pnu_spell_check(text):

    checked_sent = text.encode('utf-8')
    list = {'text1': [checked_sent]}
    url = "http://speller.cs.pusan.ac.kr/results"
    r = requests.post(url, data=list)
    html = r.text

    orgList = []
    corList = []
    target = '"orgStr":"'
    find_by_html(html, target, orgList)
    target = '"candWord":"'
    find_by_html(html, target, corList)

    # 리스트 개수가 다를 경우를 위해 예외 처리
    if len(orgList) == len(corList):
        for o, c in zip(orgList, corList):
            if c == '':
                return ''
            else:
                # 대치어가 여러 개 나왔을 때 가장 첫 번째 값을 할당함
                if c.find('|') == -1: text = text.replace(''+o, ''+c)
                else: text = text.replace(''+o, ''+c.split('|')[0])
    return text


@app.route('/pos', methods = ['POST'])
def pos():
    x = request.json #json 데이터를 받아옴
    print(x)
    requestText = x['text'] # 형태소 분석할 텍스트

    # ------------------------형태소 분석 로직---------------------------
    m = Mecab()
    checked_sent = requestText


    # 오탈자 전처리
    non_blank_checked_sent = checked_sent.replace(" ", "")  # 공백 제거
    temp_sent = pnu_spell_check(non_blank_checked_sent)

    # 일반적으로 공백 전처리 후 실행하는 것이 성능이 더 좋았지만
    # 간혹 아닌 correction 값이 아예 나오지 않는 경우도 있어 해당 경우는
    # 원래 공백이 있는 상태의 입력값으로 처리
    if temp_sent != '':
        checked_sent = temp_sent
    else:
        checked_sent = pnu_spell_check(checked_sent)


    r = m.pos(checked_sent)
    print(r)
    # result = ''.join(r)
    # ------------------------형태소 분석 로직 끝---------------------------

    return jsonify(result=r) # 받아온 데이터를 다시 전송
    
@app.route('/environments/<language>')
def environments(language):
    return jsonify({"language":language})    
 
if __name__ == "__main__":
    app.run(host='0.0.0.0', port = 5000)

