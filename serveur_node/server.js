var io = require('socket.io').listen(8080);
var mysql = require('mysql');
var connection = mysql.createConnection({
    host     : 'localhost',
    user     : 'userChatAndroid',
    password : '0123456789',
    database : 'chatandroid'
});

connection.connect(function(err){
    if(!err) {
	console.log("Database is connected");    
    } else {
	console.log("Error connecting database");    
    }
});

connection.query('SELECT * FROM Log', function(err, rows, fields) {
    if (!err){
	console.log('Reponse a la requete : SELECT * FROM Log -> ', rows);
	console.log('Nombre de reponses : ' + rows.length);
    }
    else{
	console.log('Erreur lors du traitement de la requete : SELECT * FROM Log');
    }
});

connection.query('SELECT * FROM Chat', function(err, rows, fields) {
    if (!err){
	console.log('Reponse a la requete : SELECT * FROM Chat -> ', rows);
	console.log('Nombre de reponses : ' + rows.length);
    }
    else{
	console.log('Erreur lors du traitement de la requete : SELECT * FROM Chat');
    }
});

connection.query('SELECT pseudo, connection FROM Log', function(err, rows, fields) {
    if (!err){
	console.log('Reponse a la requete : SELECT pseudo, connection FROM Log -> ', rows);
	console.log('Nombre de reponses : ' + rows.length);
    }
    else{
	console.log('Erreur lors du traitement de la requete : SELECT pseudo, connection FROM Log');
    }
});

io.sockets.on('connection', function (socket) {
    socket.on('user log', function (msg) {
	if(msg.typeConnexion == "connexion"){
	    //gestion de la tentavive de connection au chat
	    connection.query('SELECT * FROM Log WHERE pseudo = \"' + msg.pseudo + '\" AND mdp = \"' + msg.mdp + '\"', function(err, rows, fields) {
		if (!err){
		    console.log('Reponse a la requete : SELECT * FROM Log WHERE pseudo = \"' + msg.pseudo + '\" AND mdp = \"' + msg.mdp + '\" -> ', rows);
		    if(rows.length == 1){
			socket.emit('user log', {answer: 'true'});
			console.log('Le client ' + msg.pseudo + ' peut se connecter au chat');
		    }
		    else if(rows.length == 0){
			socket.emit('user log', {answer: 'false'});
			console.log('Les identifiants du client ' + msg.pseudo + ' sont incorrects');
		    }
		    else{
			socket.emit('user log', {answer: 'false'});
			console.log('Probleme dans la base de donnée : Un pseudo doit etre unique (' + msg.pseudo + ')');
		    }
		}
		else{
		    socket.emit('user log', {answer: 'false'});
		    console.log('Erreur lors du traitement de la requete : SELECT * FROM Log WHERE pseudo = \"' + msg.pseudo + '\" AND mdp = \"' + msg.mdp + '\"');
		}
	    });
	}
	else{
	    //gestion de la tentavive d'inscription au chat
	    connection.query('SELECT * FROM Log WHERE pseudo = \"' + msg.pseudo + '\"', function(err, rows, fields) {
		if (!err){
		    console.log('Reponse a la requete : SELECT * FROM Log WHERE pseudo = \"' + msg.pseudo + '\" -> ', rows);
		    if(rows.length == 0){
			connection.query('INSERT INTO Log VALUES (\"' + msg.pseudo + '\", \"' + msg.mdp + '\", 1, \"\", \"\")', function(err, rows, fields) {
			    if (!err){
				socket.emit('user log', {answer: 'true'});
				console.log('Reponse a la requete : INSERT INTO Log VALUES (\"' + msg.pseudo + '\", \"' + msg.mdp + '\", 1)', rows);
				console.log('Le client ' + msg.pseudo + ' vient de s\'inscrire et peut se connecter au chat');
			    }
			    else{
				socket.emit('user log', {answer: 'false'});
				console.log('Erreur lors du traitement de la requete : INSERT INTO Log VALUES (\"' + msg.pseudo + '\", \"' + msg.mdp + '\")');
			    }
			});
		    }
		    else if(rows.length == 1){
			socket.emit('user log', {answer: 'false'});
			console.log('Un client nomme ' + msg.pseudo + ' existe deja');
		    }
		    else{
			socket.emit('user log', {answer: 'false'});
			console.log('Probleme dans la base de donnée : Un pseudo doit etre unique (' + msg.pseudo + ')');
		    }
		}
		else{
		    socket.emit('user log', {answer: 'false'});
		    console.log('Erreur lors du traitement de la requete : SELECT * FROM Log WHERE pseudo = \"' + msg.pseudo + '\"');
		}
	    });
	}
    });

    //un utilsateur envoie un message aux autres
    socket.on('user message', function (msg) {
	console.log('Envoi du message : ' + msg.message + ' de la part de : ' + msg.pseudo + ' aux autres clients, type de message : ' + msg.type_message);
	connection.query('INSERT INTO Chat (auteur, message, type_message, temps) VALUES (\"' + msg.pseudo  + '\", \"' + msg.message  + '\", \"' + msg.type_message + '\", \"' + msg.temps + '\")', function(err, rows, fields) {
	    if (!err){
		connection.query('SELECT picture FROM Log WHERE pseudo = \"' + msg.pseudo + '\"', function(err1, rows1, fields1) {
		    if (!err1){
			socket.broadcast.emit('user message', {message: msg.message, pseudo: msg.pseudo, type_message: msg.type_message, temps : msg.temps, picture_profile: rows1[0].picture});
			console.log('Reponse a la requete : SELECT picture FROM Log WHERE pseudo = \"' + msg.pseudo + '\" -> ', rows1);
		    }
		    else{
			console.log('Erreur lors du traitement de la requete : SELECT picture FROM Log WHERE pseudo = \"' + msg.pseudo + '\"');
		    }
		});
		console.log('Reponse a la requete : INSERT INTO Chat (auteur, message, type_message, temps) VALUES (\"' + msg.pseudo  + '\", \"' + msg.message  + '\", \"' + msg.type_message + '\", \"' + msg.temps + '\") -> ', rows);
		console.log('Le message : ' + msg.message + ' de ' + msg.pseudo + ', type de message : ' + msg.type_message + ' a ete ajoute a la base de donnees');
	    }
	    else{
		console.log('Erreur lors du traitement de la requete : INSERT INTO Chat (auteur, message, type_message, temps) VALUES (\"' + msg.pseudo  + '\", \"' + msg.message  + '\", \"' + msg.type_message + '\", \"' + msg.temps + '\")');
	    }
	});
    });

    //un utilisateur veut obtenir toute la conversation
    socket.on('user conversation', function (msg) {
	console.log('Un client souhaite recuperer la conversation');
	connection.query('SELECT * FROM Chat', function(err, rows, fields) {
	    if (!err){
		console.log('Reponse a la requete : SELECT * FROM Chat -> ', rows);
		connection.query('SELECT * FROM Log', function(err1, rows1, fields1) {
		    if (!err1){
			console.log('Reponse a la requete : SELECT * FROM Log -> ', rows1);
			socket.emit('user conversation', {conversation: rows, pictures_profile: rows1});
			console.log('La conversation a ete envoye');
		    }
		    else{
			console.log('Erreur lors du traitement de la requete : SELECT * FROM Log');
			socket.emit('user conversation', {conversation: [], pictures_profile: []});
		    }
		});
	    }
	    else{
		console.log('Erreur lors du traitement de la requete : SELECT * FROM Chat');
		socket.emit('user conversation', {conversation: [], pictures_profile: []});
	    }
	});
    });

    //un utilisateur demande l'état de connection de tous les utilisateurs
    socket.on('users get_state_connection', function (msg) {
	console.log('un utilisateur demande l\'état de connection de tous les utilisateurs');
	connection.query('SELECT pseudo, connection, picture, description FROM Log', function(err, rows, fields) {
	    if (!err){
		socket.emit('users get_state_connection', {users: rows});
		console.log('Reponse a la requete : SELECT pseudo, connection, picture FROM Log -> ', rows);
		console.log('Les états de connection ont ete envoye');
	    }
	    else{
		socket.emit('users get_state_connection', {users: []});
		console.log('Erreur lors du traitement de la requete : SELECT pseudo, connection, picture FROM Log');
	    }
	});
    });

    //un utilisateur change son état de connection
    socket.on('user set_state_connection', function (msg) {
	console.log('Un client change son état de connection');
	//informe les aux autres utilisateurs
	connection.query('update Log set connection = ' + msg.stateConnection + ' where pseudo = \"' + msg.pseudo + '\"', function(err, rows, fields) {});
	connection.query('SELECT pseudo, connection FROM Log', function(err, rows, fields) {
	    if (!err){
		socket.broadcast.emit('users get_state_connection', {users: rows});
		console.log('Reponse a la requete : SELECT pseudo, connection FROM Log -> ', rows);
		console.log('Les états de connection ont ete envoye');
	    }
	    else{
		socket.broadcast.emit('users get_state_connection', {users: []});
		console.log('Erreur lors du traitement de la requete : SELECT pseudo, connection FROM Log');
	    }
	});
    });

    //un utilisateur se déconnecte
    socket.on('disconnect', function () {
	socket.emit('user disconnected');
    });

    //un utilisateur se désinscrit
    socket.on('user unsubscribe', function (msg) {
	console.log('Un client se désinscrit');
	connection.query('delete from Chat where auteur = \"' + msg.pseudo + '\"', function(err, rows, fields) {
	    if(!err){
		console.log('delete from Chat where auteur = \"' + msg.pseudo + '\" -> ', rows);
		connection.query('delete from Log where pseudo = \"' + msg.pseudo + '\"', function(err2, rows2, fields2) {
		    if(!err2){
			socket.emit('user unsubscribe', {answer: true});
			console.log('delete from Log where pseudo = \"' + msg.pseudo + '\" -> ', rows2);
			console.log('L\'utilisateur est désinscrit');
		    }
		    else{
			socket.emit('user unsubscribe', {answer: false});
			console.log('Erreur lors du traitement de la requete : delete from Chat where auteur = \"' + msg.pseudo + '\"');
		    }
		});
	    }
	    else{
		console.log('Erreur lors du traitement de la requete : delete from Log where pseudo = \"' + msg.pseudo + '\"');
		socket.emit('user unsubscribe', {answer: false});
	    }
	});
    });
    
    //un utilisateur change sa photo de profil
    socket.on('user picture_profile', function (msg) {
	console.log('Un client change sa photo de profil');
	connection.query('update Log set picture = \"' + msg.picture_profile + '\" where pseudo = \"' + msg.pseudo + '\"', function(err, rows, fields) {
	    if(!err){
		socket.broadcast.emit('user picture_profile', {pseudo: msg.pseudo, picture_profile: msg.picture_profile});
		console.log('update Log set picture = \"' + msg.picture_profile + '\" where pseudo = \"' + msg.pseudo + '\" -> ', rows);
	    }
	    else{
		console.log('Erreur lors du traitement de la requete : update Log set picture = \"' + msg.picture_profile + '\" where pseudo = \"' + msg.pseudo + '\"');
	    }
	});
    });

    //un utilisateur veut récupérer ses informations de profil
    socket.on('user get profile', function (msg) {
	console.log('Un client veut récupérer ses informations de profil');
	connection.query('select * from Log where pseudo = \"' + msg.pseudo + '\"', function(err, rows, fields) {
	    if(!err){
		socket.emit('user get profile', {profile: rows});
		console.log('select * from Log where pseudo = \"' + msg.pseudo + '\" -> ', rows);
	    }
	    else{
		console.log('Erreur lors du traitement de la requete : select * from Log where pseudo = \"' + msg.pseudo + '\"');
	    }
	});
    });
    
    //un utilisateur veut changer son pseudo
    socket.on('user set pseudo', function (msg) {
	console.log('Un utilisateur veut changer son pseudo');
	connection.query('select * from Log where pseudo = \"' + msg.newPseudo + '\"', function(err, rows, fields) {
	    if(!err){
		if(rows.length == 0){
		    connection.query('update Log set pseudo = \"' + msg.newPseudo + '\" where pseudo = \"' + msg.pseudo + '\"', function(err1, rows1, fields1) {
			if(!err1){
			    socket.emit('user set pseudo', {newPseudo: msg.newPseudo, answer: 'true'});
			}
			else{
			    socket.emit('user set pseudo', {newPseudo: msg.newPseudo, answer: 'false'});
			}
		    });
		}
		else{
		    socket.emit('user set pseudo', {newPseudo: msg.newPseudo, answer: 'false'});
		}
	    }
	    else{
		socket.emit('user set pseudo', {newPseudo: msg.newPseudo, answer: 'false'});
	    }
	});
    });
    
    //un utilisateur veut changer son mot de passe
    socket.on('user set password', function (msg) {
	console.log('Un utilisateur veut changer son mot de passe');
	connection.query('update Log set mdp = \"' + msg.password + '\" where pseudo = \"' + msg.pseudo + '\"', function(err, rows, fields) {
	    if(!err){
		socket.emit('user set password', {mdp: msg.password, answer: 'true'});
	    }
	    else{
		socket.emit('user set password', {mdp: msg.password, answer: 'false'});
	    }
	});
    });
    
    //un utilisateur veut changer sa description
    socket.on('user set description', function (msg) {
	console.log('Un utilisateur veut changer sa description');
	connection.query('update Log set description = \"' + msg.description + '\" where pseudo = \"' + msg.pseudo + '\"', function(err, rows, fields) {
	    if(!err){
		socket.emit('user set description', {answer: 'true'});
	    }
	    else{
		socket.emit('user set description', {answer: 'false'});
	    }
	});
    });
});
