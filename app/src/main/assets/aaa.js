var ecc = require('eosjs-ecc')
var ByteBuffer = require('bytebuffer')
var AAA = require('aaajs')

config = {
  keyProvider: ['5JWYoMqLxGAmHi5BnhYRSdaTpNsF4jzcUCgKq57LMHqHqnCGJn4','5KZyaoA9W2N6CP7EDoYBXXVMySVm1ZswVte2beByL2SD1C1cnEk'], // WIF string or array of keys..
  chainId: '1c6ae7719a2a3b4ecb19584a30ff510ba1b6ded86e1fd8b8fc22f1179c622a32',
  httpEndpoint: 'http://47.98.107.96:10180', // jungle testnet
  expireInSeconds: 60,
  broadcast: true,
  verbose: false, // API activity
  sign: true
}


aaa = AAA(config)

getBalance = function(accountName){
  aaa.getCurrencyBalance('eosio.token', accountName, 'AAA',function(error,result){
     window.aaaChain.getAAABalance(error,result[0])
    })
}

getPublicKey = function(privateKey){
  var publicKey = ecc.privateToPublic(privateKey,'AAA')
  window.aaaChain.getPublicKey(publicKey)
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

  var enKey  = ecc.Aes.encrypt(privateKey,ecc.PublicKey(anotherPublicKey, 'AAA'),key)
  console.log("enKey:"+JSON.stringify(enKey))

  window.aaaChain.getEncryptKey(JSON.stringify(enKey))
}

decryptKey = function(privateKey,anotherPublicKey,enKey){
  var obj = eval('(' + enKey + ')')
  var nonce = ByteBuffer.Long.fromValue(obj.nonce)
  var message = Buffer.from(obj.message)
  var checksum = obj.checksum
  var deKey  = ecc.Aes.decrypt(privateKey,ecc.PublicKey(anotherPublicKey, 'AAA'),nonce,message,checksum)
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