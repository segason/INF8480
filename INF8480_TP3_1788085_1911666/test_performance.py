import requests
import time
import threading

completion_time = 0;

class MyThread(threading.Thread):

    def run(self):
        
            time_start = time.time();
            print('debut ', time_start);
            print("{} started!".format(self.getName()))              # "Thread-x started!"
            r = requests.get('http://132.207.12.198:8080?nom=Mohamed')                                 # Pretend to work for a second
            print (r.text)
            print("{} finished!".format(self.getName()))             # "Thread-x finished!"
            time_to_complete = time.time() - time_start;
            
            global completion_time
            completion_time += time_to_complete;
            
            

if __name__ == '__main__':
    threads=[]
    for x in range(30):                                     # Four times...
        mythread = MyThread(name = "requete-{}".format(x + 1))  # ...Instantiate a thread and pass a unique ID to it
        threads.append(mythread)
    for thread in threads:
        thread.start()
    for thread in threads:                                     # Four times...
        thread.join()


average = completion_time / 30;
print('duree moyenne: ',average);
