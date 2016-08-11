// Import express
var express = require('express');
var bodyParser = require('body-parser');
var app = express();

// Load config for RethinkDB and express
var config = require("config.js");

var r = require('rethinkdb');

app.use(express.static('public'));
app.use(bodyParser());

// Middleware that will create a connection to the database
app.use(createConnection);

// Define main routes
app.route('/opsData/get').get(get);
app.route('/opsData/getZeroCounts').get(getZeroCounts);
app.route('/opsData/getOneCounts').get(getOneCounts);

// Middleware to close a connection to the database
app.use(closeConnection);
/*
 * Retrieve all todos
 */
function get(req, res, next) {
    r.table('ops_data').run(req._rdbConn).then(function(cursor) {
        debugger;
        return cursor.toArray();
        //cursor.toArray(function(err, result) {
        //     if (err) throw err;
        //     console.log(JSON.stringify(result, null, 2));
        // });
    }).then(function(result) {
        res.send(JSON.stringify(result));
    }).error(handleError(res))
    .finally(next);
}

function getOneCounts(req, res, next) {
    r.table("ops_data").filter({result:1}).count().run(req._rdbConn)
      .then(function(result) {
        return result;
        //cursor.toArray(function(err, result) {
        //     if (err) throw err;
        //     console.log(JSON.stringify(result, null, 2));
        // });
    }).then(function(result) {
        res.send(JSON.stringify(result));
    }).error(handleError(res))
    .finally(next);

}

function getZeroCounts(req, res, next) {
    r.table("ops_data").filter({result:0}).count().run(req._rdbConn)
      .then(function(result) {
        return result;
        //cursor.toArray(function(err, result) {
        //     if (err) throw err;
        //     console.log(JSON.stringify(result, null, 2));
        // });
    }).then(function(result) {
        res.send(JSON.stringify(result));
    }).error(handleError(res))
    .finally(next);

}

function startExpress() {
    app.listen(config.express.port);
    console.log('Listening on port '+config.express.port);
}
/*
 * Create tables/indexes then start express
 */
r.connect(config.rethinkdb, function(err, conn) {
    if (err) {
        console.log("Could not open a connection to initialize the database");
        console.log(err.message);
        process.exit(1);
    }

r.table('ops_data').run(conn).then(function(err, result) {
        console.log("Table is available, starting express...");
        startExpress();
        r.table('ops_data').changes().run(conn, function(err, cursor) {
          console.log("Some thing changes");
          cursor.each(console.log);
        });
    }).error(function(err) {
        // The database/table/index was not available, create them
        r.tableCreate('ops_data').run(conn);
    });
});


function createConnection(req, res, next) {
    r.connect(config.rethinkdb).then(function(conn) {
        req._rdbConn = conn;
        next();
    }).error(handleError(res));
}

/*
 * Close the RethinkDB connection
 */
function closeConnection(req, res, next) {
    req._rdbConn.close();
}
function handleError(res) {
    return function(error) {
        res.send(500, {error: error.message});
    }
}
