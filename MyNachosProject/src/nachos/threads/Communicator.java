package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	communicatorLock = new Lock();
    	speakerCondition = new Condition(communicatorLock);
		listenerCondition = new Condition(communicatorLock);
		hasMessage = false;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	communicatorLock.acquire();
    	message = word;
    	hasMessage = true;
    	if(listenerQueue.isEmpty()){
    		speakerQueue.offer(KThread.currentThread());
    		speakerCondition.sleep();
    		
    		speakerQueue.poll();
    		System.out.println(KThread.currentThread() + " speaks " + word);
    		communicatorLock.release();
    		return;    		
    	}
    	else{
    		listenerCondition.wake();
    		listenerQueue.poll();
    		System.out.println(KThread.currentThread() + " speaks " + word);
    		communicatorLock.release();
    		return;	
    	}
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	communicatorLock.acquire();
    	if(speakerQueue.isEmpty()){
    		listenerQueue.offer(KThread.currentThread());
    		listenerCondition.sleep();
    		
    		listenerQueue.poll();
    		System.out.println(KThread.currentThread() + " listens " + message);
    		hasMessage = false;
    		communicatorLock.release();
    		return message;
    	}
    	else{
    		speakerCondition.wake();
    		speakerQueue.poll();
    		
    		System.out.println(KThread.currentThread() + " listens " + message);
    		hasMessage = false;
    		communicatorLock.release();
    		return message;
    	}
    }
    
    private Lock communicatorLock;
    private Condition speakerCondition;
    private Condition listenerCondition;
    private LinkedList<KThread> speakerQueue = new LinkedList<KThread>();
    private LinkedList<KThread> listenerQueue = new LinkedList<KThread>();
    boolean hasMessage;
    int message;
}
