var Eos = require('eosjs')
var ecc = require('eosjs-ecc')
var ByteBuffer = require('bytebuffer')
var AAA = require('aaajs')

config = {
  keyProvider: ['5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV','5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5'], // WIF string or array of keys..
  chainId: '038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca',
  httpEndpoint: 'http://jungle.cryptolions.io:18888', // jungle testnet
  expireInSeconds: 60,
  broadcast: true,
  verbose: false, // API activity
  sign: true
}


//eos = Eos(config)

aaa = AAA(config)

getBalance = function(accountName){
  aaa.getCurrencyBalance('eosio.token', accountName, 'EOS',function(error,result){
     window.aaaChain.getEosBalance(error,result)
    })
}

getSignature = function(json,privateKey){
  var key = ecc.sign(json,privateKey)
  console.log("key----"+key)
  window.aaaChain.getSignature(key)
}

// 买家预付款
prepay= function(id,buyer,seller,price){

  aaa.payForGood(id, buyer, seller, price).
    then(value => {
      console.log('payForGood OK. txid: ' + value.transaction_id)
      window.aaaChain.prepay(JSON.stringify(value))
      }).catch(e => {
        console.log("payForGood failed: " + e)
         window.aaaChain.prepayError(e)
        });
}


encryptKey = function(privateKey,anotherPublicKey,key){

  var enKey  = ecc.Aes.encrypt(privateKey,anotherPublicKey,key)
  console.log("enKey:"+JSON.stringify(enKey))

  window.aaaChain.getEncryptKey(JSON.stringify(enKey))
}

decryptKey = function(privateKey,anotherPublicKey,enKey){
  var obj = eval('(' + enKey + ')')
  var nonce = ByteBuffer.Long.fromValue(obj.nonce)
  var message = Buffer.from(obj.message)
  var checksum = obj.checksum
  var deKey  = ecc.Aes.decrypt(privateKey,anotherPublicKey,nonce,message,checksum)
  console.log("deKey:"+deKey.toString())
  window.aaaChain.getDecryptKey(deKey.toString())
}


getPrepayStatus = function(id) {
  // json, code, scope, table, table_key
  contract = 'aaatrust1111';
  scope = 'aaatrust1111';
  table = 'records';
  table_key = 'id';
  lower_bound = id;
  upper_bound = id + 1;
  aaa.getTableRows(true, contract, scope, table, table_key, lower_bound, upper_bound).
    then(result => {
    console.log(result.rows);
    window.aaaChain.paySuccessStatus(result.rows)
     }).
    catch(e => {
    console.log("print_row failed: " + e)
    window.aaaChain.payFailureStatus(e)
    });
}


// 买家确认付款
confirmOrder = function(buyer,orderid) {

  aaa.confirmPayment(buyer, orderid).then(value => {
    console.log('confirmPayment OK. txid: ' + value.transaction_id)
    window.aaaChain.paySuccess(JSON.stringify(value))
    }).catch(e => {
      console.log("confirmPayment failed: " + e)
       window.aaaChain.payFailure(e)
    })
}