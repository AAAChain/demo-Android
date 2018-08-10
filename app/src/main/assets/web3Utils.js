var Web3 = require('web3')
var web3 = new Web3();

//web3.setProvider(new web3.providers.HttpProvider('http://localhost:8540'));
web3.setProvider(new web3.providers.HttpProvider('https://rinkeby.infura.io/YMJVwtsOg1JeHYzvNlbw'));

console.log("what is why")
createAccount = function(password){
  web3.eth.personal.newAccount(password,function (error, result) {
       console.log("newAccount-------"+result)
       window.aaa.createAccount()
   })
}

getAccounts = function(){
  web3.eth.getAccounts(function (error,result) {
      console.log("account:"+result + "---error:"+error)
      window.aaa.getAccounts(error,result)
  })
}

getBalance1 = function(address){
  console.log("address:"+address)
  web3.eth.getBalance(address,function(error,result){
    console.log("js balance:"+result+ "---error:"+error)
    window.aaa.getBalance1(error,web3.utils.fromWei(result,'ether'))
  })
}
getCoinbase = function(){
  web3.eth.getCoinbase(function(error,result){
      console.log("coin base:"+result+ "---error:"+error)
      window.aaa.getCoinbase(error,result)
  })
}
getTransaction = function(txHash){

  web3.eth.getTransaction(txHash,function (error, result) {
    console.log("getranaction----"+result.from)
    window.aaa.getTransaction(error,result.from)
  })
}
