var Eos = require('eosjs');

config = {
  // chainId: 'cf057bbfb72640471fd910bcb67639c22df9f92470936cddc1ade0e2f2e7dc4f', // 32 byte (64 char) hex string
  // keyProvider: ['5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3'], // WIF string or array of keys..
  // httpEndpoint: 'http://47.98.107.96:8888',
  chainId: '038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca',
  // keyProvider: ['5JohomNquqUiX6Ue6qqXevy3u3qSLXX8KokicBNFdSMNdG9Aepi', '5HtN3Vy2gfmebN7uCU9mcN3VPNNNTE28vinUP11wR93T8S5Kn7j'], // WIF string or array of keys..
  httpEndpoint: 'http://193.93.219.219:8888', // jungle testnet
  expireInSeconds: 60,
  broadcast: true,
  verbose: false, // API activity
  sign: true
}

eos = Eos(config)
console.log("eos-----"+config)
accountName = 'aaauser1'

getBalance = function(){
  eos.getCurrencyBalance('eosio.token', accountName, 'EOS')
    .then(result => {
    console.log(result)
    window.aaa.getEosBalance(result)
    })
    .catch(error => console.error(error))
}