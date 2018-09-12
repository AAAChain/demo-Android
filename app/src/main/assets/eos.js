var Eos = require('eosjs');
var ecc = require('eosjs-ecc')

config = {
  keyProvider: ['5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV','5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5'], // WIF string or array of keys..
  chainId: '038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca',
  httpEndpoint: 'http://jungle.cryptolions.io:18888', // jungle testnet
  expireInSeconds: 60,
  broadcast: true,
  verbose: false, // API activity
  sign: true
}


eos = Eos(config)


getBalance = function(accountName){
console.log("acount:"+accountName)
  eos.getCurrencyBalance('eosio.token', accountName, 'EOS',function(error,result){

   window.aaa.getEosBalance(error,result)
  })
}

getSignature = function(json,privateKey){
  console.log("getSignature--------------"+privateKey)
  var key = ecc.sign(json,privateKey)
  console.log("key----"+key)
  window.aaa.getSignature(key)
}

//转账到中间账号
prepay= function(id,account1,account2,price){
  const options = { authorization: [ account1+`@active` ] };
  console.log("repay id:"+id+"--account1:"+account1+"--account2:"+account2+"--price:"+price);
   eos.contract('aaatrust1111').then(contract => {
      //contract.prepay(111, "aaauser1", "aaauser2", "2.0000 EOS" , options).
      contract.prepay(id, account1, account2, price , options).
        then(value => {
        console.log('prepay OK')
        window.aaa.prepay(JSON.stringify(value))
        }).
        catch(e => {
        console.log("prepay failed: " + e)
        window.aaa.prepayError(e)
        })
    })
}


getPrepayStatus = function(id) {
  // json, code, scope, table, table_key
  contract = 'aaatrust1111';
  scope = 'aaatrust1111';
  table = 'records';
  table_key = 'id';
  lower_bound = id;
  upper_bound = id + 1;
  eos.getTableRows(true, contract, scope, table, table_key, lower_bound, upper_bound).
    then(result => {
    console.log(result.rows);
    window.aaa.paySuccess(result.rows)
     }).
    catch(e => {
    console.log("print_row failed: " + e)
    window.aaa.payFailure(e)
    });
}



confirmOrder = function(accountName,orderid) {
  const options = { authorization: [ accountName + `@active` ] };
  eos.contract('aaatrust1111',function (error, result) {
      result.confirm(orderid,options,function (error, result) {
      if(error == null){
      console.log("result:"+JSON.stringify(result))
      }else{
      console.log("error:"+error)
      }
      })
  })
}