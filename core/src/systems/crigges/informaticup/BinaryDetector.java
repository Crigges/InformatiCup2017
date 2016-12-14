package systems.crigges.informaticup;

public class BinaryDetector {
	
	public static boolean isBinaryFile(byte[] arr){
	    int size = arr.length;
	    if(size > 1024){
	    	size = 1024;
	    } 
	    int ascii = 0;
	    int other = 0;
	    
	    for(int i = 0; i < size; i++) {
	        byte b = arr[i];
	        if( b < 0x09 ) return true;

	        if( b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D ) ascii++;
	        else if( b >= 0x20  &&  b <= 0x7E ) ascii++;
	        else other++;
	    }
	    if( other == 0 ) return false;

	    return 100 * other / (ascii + other) > 95;
	}

}
