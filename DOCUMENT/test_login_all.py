import urllib.request
import urllib.parse
import http.cookiejar
import re

accounts = [
    ('admin', '123456', '/admin/dashboard'),
    ('manager', '123456', '/admin/dashboard'),
    ('user', '123456', '/'),
    ('vendor_demo', '123456', '/vendor/dashboard'),
    ('vendor_fahasa', '123456', '/vendor/dashboard')
]

for username, password, expected in accounts:
    cj = http.cookiejar.CookieJar()
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))
    
    # Get login page
    login_html = opener.open('http://localhost:8080/login').read().decode('utf-8')
    m = re.search(r'name="_csrf"\s+value="([^"]+)"', login_html)
    csrf = m.group(1) if m else ''
    
    data = {'username': username, 'password': password}
    if csrf:
        data['_csrf'] = csrf
    
    encoded = urllib.parse.urlencode(data).encode('utf-8')
    req = urllib.request.Request('http://localhost:8080/process-login', data=encoded, method='POST')
    resp = opener.open(req)
    final_url = resp.geturl()
    
    cookie_names = [c.name for c in cj]
    has_jwt = 'the4book_jwt' in cookie_names
    print(f"User: {username:15} | Status: OK | URL: {final_url} | Has JWT: {has_jwt}")
