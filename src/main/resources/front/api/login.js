function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }
// cpy update
/*function sendMsgApi(data) {
    return $axios({
        'url': '/user/sendMsg',
        'method': 'post',
        data
    })
}*/

function loginoutApi() {
  return $axios({
    'url': '/user/logout',
    'method': 'post',
  })
}

  