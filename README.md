# Client-side-application
Fun project for uni

Commands to work with server:
-ct - connect to server                                                                                                  
-dc - disconnect from server                                                                                             
-sobjc - send Board.graphObjectVector to server in JSON format                                                           
-sobjs - cannot be executed directly from client, identifies that server sent object                                     
-clrc - clears storage of objects on server                                                                              
-clrs - cannot be executed directly from client, identifies that server requested to clear storage of objects on client  
-vecsc - requests size of objects storage on server                                                                      
-vecss - cannot be executed directly from client, identifies that server requested size of objects storage on client     
-gobjc - request of object with given number ex: -gobjc3                                                                 
-gobjs - cannot be executed directly from client, identifies that server requested object with given number ex:-gobjs3   
-robj - cannot be executed directly from client, identifies that server sent requested object by command -gobjc          
