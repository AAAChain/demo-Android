var ipfsAPI = require('ipfs-api')
var ipfs = ipfsAPI({host: '47.98.107.96', port: '22222', protocol: 'http'})

 add = function(index) {
 const descBuffer = Buffer.from("hello"+index)
     ipfs.add(Buffer.from(descBuffer), function (err, res) {
          console.log("------err-----"+err+"-----res-------"+res)
         if (err || !res) {
             return console.error('ipfs add error', err, res)
         }

         res.forEach(function (file) {
             if (file && file.hash) {
                 console.log('successfully stored', file.hash)
                 display(file.hash)
             }
         })
     })
 }

  display = function(hash) {
   // buffer: true results in the returned result being a buffer rather than a stream
   ipfs.cat(hash, {buffer: true}, function (err, res) {
     if (err || !res) {
       return console.error('ipfs cat error', err, res)
     }

    window.aaa.displayIpfs(hash,res.toString())
   })
  }