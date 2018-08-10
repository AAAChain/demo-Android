var w3ajs_ws = require("w3ajs-ws");
require("w3ajs");
var Apis = w3ajs_ws.Apis;

let witness = "ws://47.98.107.96:21012";

testok = function(){
  Apis.instance(witness, true).init_promise.then((res) => {
      Apis.instance().db_api().exec( "get_account_by_name", [ "test" ]).then(function(account) {
        console.log(account);
         window.aaa.testok(account);
      })
  });
}