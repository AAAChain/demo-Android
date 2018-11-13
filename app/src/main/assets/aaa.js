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

//auth
authPermission = function(account){
console.log("buyer:"+account)
  aaa.authPermission(account).then(value => {
    console.log('authPermisson OK. txid: ' + value.transaction_id)
    window.aaaChain.authPermissionSuccess()
  }).catch(e => {
    console.log("authPermisson failed: " + e)
     window.aaaChain.authPermissionError(e)
  });
}


getBalance = function(accountName){
  aaa.getCurrencyBalance('eosio.token', accountName, 'AAA',function(error,result){
     window.aaaChain.getAAABalance(error,result[0])
    })
}

getPublicKey = function(privateKey){
  var publicKey = ecc.privateToPublic(privateKey,'AAA')
  window.aaaChain.getPublicKey(publicKey)
}

generationSecretKey = function(){

 ecc.randomKey().then(privateKey => {

     window.aaaChain.generationSecretKey(privateKey,ecc.privateToPublic(privateKey,'AAA'))
 })
}


transfer = function(from,to,amount,remark){
  var options = {authorization:from+'@active',broadcast:true,sign:true}
  aaa.transfer({from:from,to:to,quantity:amount,memo:remark},options,function (error,result) {
     window.aaaChain.transfer(JSON.stringify(result),error)
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


//获取eoshackathon账户的信息
getAccountInfo = function(account){

  aaa.getAccount({account_name: account}, function (error, result) {
          window.aaaChain.getAccountInfo(JSON.stringify(result),error)
      })
}


getRamPrice = function(){

  aaa.getTableRows({"json": true,"code": "eosio","scope":"eosio","table":"rammarket"},function (error, result) {
      const quote = result.rows[0].quote.balance.substring(0,result.rows[0].quote.balance.lastIndexOf(" "));
      const base = result.rows[0].base.balance.substring(0,result.rows[0].base.balance.lastIndexOf(" "));

      window.aaaChain.getRamPrice(parseFloat(quote/(base/1024)).toFixed(6),error)
  })
}


//抵押
mortgage = function(account,receiveAccount,netq,cpuq){

  aaa.transaction(result =>
    result.delegatebw({
      from:account,
      receiver:receiveAccount,
      stake_net_quantity:netq,
      stake_cpu_quantity:cpuq,
      transfer:0
    })
  ).then(result=>
           window.aaaChain.mortgageSuccess(JSON.stringify(result))
       ).catch(error=>
           window.aaaChain.mortgageError(error.toString())
       )
}


//赎回
redemption = function(account,receiveAccount,netq,cpuq){

  aaa.transaction(result => {
      result.undelegatebw({
          from: account,
          receiver: receiveAccount,
          unstake_net_quantity: netq,
          unstake_cpu_quantity: cpuq
      })
  }).then(result=>
            window.aaaChain.redemptionSuccess(JSON.stringify(result))
        ).catch(error=>
        window.aaaChain.redemptionError(error.toString())
        )

}

  /**购买**/
buyram = function(account,receiveAccount,bytesnum){

  aaa.transaction(tr => {
      tr.buyrambytes({
          payer: account,
          receiver: receiveAccount,
          bytes: bytesnum
      })
  }).then(result=>{

    window.aaaChain.buyramSuccess(JSON.stringify(result))
  }).catch(error=>{
  console.log("buy ram error:"+error)
    window.aaaChain.buyramError(error.toString())
  })

}


//出售
sellram = function(account,bytesnum){

  aaa.transaction(tr => {
      tr.sellram({
          account: account,
          bytes: bytesnum
      })
  }).then(result=>
        window.aaaChain.sellramSuccess(JSON.stringify(result))
    ).catch(error=>
    window.aaaChain.sellramError(error.toString())
    )
}