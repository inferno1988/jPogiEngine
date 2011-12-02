package iipimage.jiipimage;
/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/*
 * CanvasImage.java
 * 
 */
/**
 * @author Denis Pitzalis 
 */
public class JIIPResponse {
	
    /**
     * The name of the object that was returned.
     */
    public String mRequest;
    
    /**
     * The object that was returned.
     */
    public String mResponse;
  
    /**
     * Creates a new <code>IIPResponse</code> object.
     * 
     * @param	request name of request that the respose is for.
     * @param	response request response from IIP server.
     */
    public JIIPResponse (String request, String response) {
	mRequest = request;
	mResponse = response;
    }
    
    /**
     * Returns a string representation of the <code>IIPResponse</code>
     * object.
     */
    public String toString () {
	return new String("JIIPResponse [ "+mRequest+", "+mResponse+" ]");
    }

}

