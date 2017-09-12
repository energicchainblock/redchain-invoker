package com.utsoft.blockchain.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.util.StringUtils;
import com.utsoft.blockchain.api.util.Constants;
/**
 * 默认加密算法hash
 * @author hunterfox
 * @date: 2017年9月12日
 * @version 1.0.0
 */
public class DefaultPwdEncoder {

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };
	
	private  String encodingAlgorithm; 
	private String characterEncoding = Constants.DEFAULT_CHARSET;

	/**
	 * SHA-256 SHA-386  SHA-512 MD5
	 * @param encodingAlgorithm
	 */
	public DefaultPwdEncoder(final String encodingAlgorithm) {
		this.encodingAlgorithm = encodingAlgorithm;
	}

	public DefaultPwdEncoder() {
		this.encodingAlgorithm  = Constants.DEFAULT_PWD_HASH_ALGORITHM;
	}
	
	 public String encode(final String password) {
		if (password == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(this.encodingAlgorithm);

			if (StringUtils.hasText(this.characterEncoding)) {
				messageDigest.update(password.getBytes(this.characterEncoding));
			} else {
				messageDigest.update(password.getBytes());
			}
			final byte[] digest = messageDigest.digest();
			return getFormattedText(digest);
		} catch (final NoSuchAlgorithmException e) {
			throw new SecurityException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 *
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private String getFormattedText(final byte[] bytes) {
		final StringBuilder buf = new StringBuilder(bytes.length * 2);

		for (int j = 0; j < bytes.length; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	public void setCharacterEncoding(final String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
}
