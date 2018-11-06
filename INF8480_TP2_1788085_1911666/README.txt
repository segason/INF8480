Pour que le programme fonctionne correctement, il faut suivre les etapes suivantes.

- Il faut d'abord lancer le service de noms avant les serveurs de calcul et le repartiteur.

- Pour lancer le service de noms:
	./serverDeNoms.sh [adresseIp du poste]
	
- Pour lancer le serveur de calcul:
	./server.sh [adresseIp du poste] [mode][adresseIp du service de noms] [taux de reponse erronee] [capacite]
	Note:
		Le mode est SECURISE ou NONSECURISE
		Le taux de reponse est entre 0 et 100
		Pour les tests non Securise il faut mettre le mode à NONSECURISE pour tous les serveurs de calcul.
		
	- Pour lancer le repartiteur:
		./repartiteur.sh [nom du fichier texte des operations] [adresseIp du service de noms] [mode] [username] [passoword]
	Note:
		Le fichier texte doit être présent dans le repertoire de lancement.