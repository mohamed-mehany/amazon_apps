var express = require('express');
var router = express.Router();
var multer = require('multer');
var fname = "";


var storage = multer.diskStorage({
    destination: function(req, file, cb) {
        cb(null, 'uploads/')
    },
    filename: function(req, file, cb) {
        fname = file.originalname;
        fname = fname.replace(/\s+/g, '');
        fname = fname;
        cb(null, fname)
    }
});

var upload = multer({ storage: storage }).single('image');


router.post('/', function(req, res) {
    upload(req, res, function(err) {
        if (err) {
            res.json(err);
        }
        res.json({
            success: true,
            message: 'image uploaded successfullyuploaded!',
            filename: fname
        });

    })
});


// router.get('/:name', function(req, resp) {
//     var path = '/Users/ahmadabdulraheem/WebstormProjects/MediaServer/uploads/users/' + req.params.name;
//     var options = {

//         headers: {
//             'x-timestamp': Date.now(),
//             'x-sent': true
//         }
//     };
//     resp.sendFile(path, options, function(err) {
//         if (err) {
//             res.json(err);
//         } else {
//             console.log('Sent:', fileName);
//         }
//     });

// });
module.exports = router;