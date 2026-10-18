import urllib.request
import urllib.parse
import http.cookiejar
import re

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# 1. Login user
login_html = opener.open('http://localhost:8080/login').read().decode('utf-8')
m = re.search(r'name="_csrf"\s+value="([^"]+)"', login_html)
csrf = m.group(1) if m else ''
data = {'username': 'user', 'password': '123456'}
if csrf:
    data['_csrf'] = csrf
opener.open(urllib.request.Request('http://localhost:8080/process-login', data=urllib.parse.urlencode(data).encode('utf-8'), method='POST'))

# Check /san-voucher
san_html = opener.open('http://localhost:8080/san-voucher').read().decode('utf-8')
print("Has bookmark_added (Da Luu):", "bookmark_added" in san_html)
print("Has bookmark_add (Luu ma):", "bookmark_add" in san_html)
