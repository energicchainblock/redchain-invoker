package com.utsoft.blockchain.api.security.bc;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Objects;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;
import com.utsoft.blockchain.api.exception.BcAddressFormatException;
import com.utsoft.blockchain.api.util.Sha256Hash;
import com.utsoft.blockchain.api.util.SuiteBase58;
import java.io.Serializable;
import java.util.Arrays;
/**
 * @author <a href="flyskyhunter@gmail.com">hunterfox</a> 
 * @date  2017年8月25日
 * @version 1.0
 */
public class VersionedCheckBytes  implements Serializable, Cloneable, Comparable<VersionedCheckBytes> {

	private static final long serialVersionUID = -6993740398829423257L;
	protected final int version;
    protected byte[] bytes;
	
    
    protected VersionedCheckBytes(String encoded) throws BcAddressFormatException {
        byte[] versionAndDataBytes = SuiteBase58.decodeChecked(encoded);
        byte versionByte = versionAndDataBytes[0];
        version = versionByte & 0xFF;
        bytes = new byte[versionAndDataBytes.length - 1];
        System.arraycopy(versionAndDataBytes, 1, bytes, 0, versionAndDataBytes.length - 1);
    }

    protected VersionedCheckBytes(int version, byte[] bytes) {
        checkArgument(version >= 0 && version < 256);
        this.version = version;
        this.bytes = bytes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionedCheckBytes other = (VersionedCheckBytes) o;
        return this.version == other.version && Arrays.equals(this.bytes, other.bytes);
    }
    
	 @Override
	 public int compareTo(VersionedCheckBytes o) {
		int result = Ints.compare(this.version, o.version);
	    return result != 0 ? result :
	    	UnsignedBytes.lexicographicalComparator().compare(this.bytes, o.bytes);
	}
	

	@Override
	public VersionedCheckBytes clone() throws CloneNotSupportedException {
	     return (VersionedCheckBytes) super.clone();
	 }
	
    /**
     * Returns the base-58 encoded String representation of this
     * object, including version and checksum bytes.
     */
    public final String toBase58() {
        // A stringified buffer is:
        //   1 byte version + data bytes + 4 bytes check code (a truncated hash)
        byte[] addressBytes = new byte[1 + bytes.length + 4];
        addressBytes[0] = (byte) version;
        System.arraycopy(bytes, 0, addressBytes, 1, bytes.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, bytes.length + 1);
        System.arraycopy(checksum, 0, addressBytes, bytes.length + 1, 4);
        return SuiteBase58.encode(addressBytes);
    }

    @Override
    public String toString() {
        return toBase58();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, Arrays.hashCode(bytes));
    }
}

