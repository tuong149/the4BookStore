import urllib.request
import urllib.parse
import http.cookiejar
import json
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

# 2. Check /api/voucher/vi
resp = opener.open('http://localhost:8080/api/voucher/vi?subtotal=500000')
vi_data = json.loads(resp.read().decode('utf-8'))
print('Wallet API response:', json.dumps(vi_data, indent=2, ensure_ascii=False))

# 3. Check /profile
profile_html = opener.open('http://localhost:8080/profile').read().decode('utf-8')
print('Voucher in profile totalVouchers:', re.findall(r'totalVouchers.*?(\d+)', profile_html))
