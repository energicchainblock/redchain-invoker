package com.utsoft.blockchain.api.security.bc;
import com.utsoft.blockchain.api.exception.BcAddressFormatException;
import static com.google.common.base.Preconditions.checkArgument;
/**
 * 赤链地址
 * @author <a href="flyskyhunter@gmail.com">hunterfox</a> 
 * @date  2017年8月25日
 * @version 1.0
 */
public class RbcAddress extends VersionedCheckBytes {

	private static final long serialVersionUID = -7581480206138304722L;
	/**
	 * @param encoded
	 * @throws AddressFormatException
	 */
	protected RbcAddress(String encoded) throws BcAddressFormatException {
		super(encoded);
	}
	
   public RbcAddress(int version, byte[] hash160) {
        super(version, hash160);
        checkArgument(hash160.length == 20, "Addresses are 160-bit hashes, so you must provide 20 bytes");
    }
}

