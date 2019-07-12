package swf.cegedim.rest.security.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTTestUtils {

	private static final Logger logger = LoggerFactory.getLogger(JWTTestUtils.class);

	private static final String CAS_ISSUER = "cas";
	private static final String CLAIM_CENTER_ID = "centerId";
	private static final String CLAIM_ROLE_DESCRIPTION = "roleDescription";
	private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_WORKPLACE_ID = "workplaceId";
	private static final String CLAIM_IP = "ip";
	private static final String CLAIM_SESSION_ID = "sessionId";
	private static final String CLAIM_USER_ID = "userId";
	private static final String CLAIM_SUB = "sub";
	private static final String CLAIM_FUNCTIONALITIES = "functionalities";
	private static final String CLAIM_ID_GROUP_CENTER = "idGroupCenter";
	private static final String CLAIM_GROUP_CENTER_LIST = "groupCenterList";
	private static final String CLAIM_GROUP_CENTER_HIERARCHY_TREE_LIST = "groupCenterHierarchyTreeList";
	private static final String CLAIM_PATIENT_SHARE_CENTER_LIST = "patientShareCenterList";

	private class TokenHelper {
		private long centerId = 1L;
		private String roleDescription = "Professionnel de Santé";
		private long role = 349L;
		private long workplaceId = 6744L;
		private String ip = "80.59.166.143, 10.25.159.5";
		private String sessionId = "_-Ps2L8d8ze_tMvoKs6aTa_J2WTfxXdyMjCquzW-";
		private long userId = 743L;
		private String sub = "test_user";
		private String[] functionalities = { "MYP_AGE_002", "MYP_AGE_003", "MYP_AGE_004", "MYP_AGE_0052", "MYP_AGE_006",
				"MYP_AGE_007", "MYP_AGE_008", "MYP_AGE_010", "MYP_AGE_011", "MYP_AGE_012", "MYP_AGE_013", "MYP_AGE_014",
				"MYP_AGE_015", "MYP_AGE_016", "MYP_AGE_017", "MYP_AGE_018", "MYP_AGE_019", "MYP_AGE_020", "MYP_AGE_021",
				"MYP_AGE_023", "MYP_AGE_029", "MYP_AGE_036", "MYP_AGE_041", "MYP_DAV_001", "MYP_HCC_001", "MYP_HCC_002",
				"MYP_IND_013", "MYP_PRS_007", "PRF_SNT", "ROLE_ACCESS", "ROLE_SUPERADMIN" };
		private long idGroupCenter = 22L;
		private Long[] groupCenterList = { 22L, 0L };
		private Long[] groupCenterHierarchyTreeList = { 2808L, 1581L, 3709L, 2807L, 1301L, 3689L, 3007L, 3549L, 3669L,
				1604L, 3572L, 3574L, 3387L, 3569L, 3570L, 3571L, 3580L, 3581L, 3582L, 3576L, 1688L, 3577L, 3578L,
				3028L };
		private Long[] patientShareCenterList = { 2808L, 1581L, 1604L, 2807L, 1301L, 3572L, 3387L, 3574L, 3007L, 3569L,
				3570L, 3571L, 22L, 3580L, 3581L, 3582L, 1688L, 3576L, 3577L, 3028L, 3578L, 3669L, 1L };
	}

	@Test
	public void testToken() {
		String jwt = JWTTestUtils.createTestToken(new TokenHelper());

		logger.info("Token creado:\n{}", jwt);
		logger.info("Header creado:\nBearer {}", jwt);
	}

	@Test
	public void customTestToken() {
		TokenHelper tokenHelper = new TokenHelper();
		tokenHelper.centerId = 8240L;

		String jwt = JWTTestUtils.createTestToken(tokenHelper);

		logger.info("Token creado:\n{}", jwt);
		logger.info("Header creado:\nBearer {}", jwt);
	}

	public static String createGenericToken() {
		JWTTestUtils jtu = new JWTTestUtils();

		return JWTTestUtils.createTestToken(jtu.new TokenHelper());
	}

	public static String createTestToken(TokenHelper tokenHelper) {
		String token = null;

		try {
			// Create the expiration time
			Date expirationTime = createExpirationTime(8);

			token = JWT.create().withIssuer(CAS_ISSUER).withExpiresAt(expirationTime)
					.withArrayClaim(CLAIM_FUNCTIONALITIES, tokenHelper.functionalities)
					.withClaim(CLAIM_SUB, tokenHelper.sub).withClaim(CLAIM_USER_ID, tokenHelper.userId)
					.withClaim(CLAIM_SESSION_ID, tokenHelper.sessionId).withClaim(CLAIM_IP, tokenHelper.ip)
					.withClaim(CLAIM_WORKPLACE_ID, tokenHelper.workplaceId).withClaim(CLAIM_ROLE, tokenHelper.role)
					.withClaim(CLAIM_ROLE_DESCRIPTION, tokenHelper.roleDescription)
					.withClaim(CLAIM_CENTER_ID, tokenHelper.centerId)
					.withClaim(CLAIM_ID_GROUP_CENTER, tokenHelper.idGroupCenter)
					.withArrayClaim(CLAIM_GROUP_CENTER_LIST, tokenHelper.groupCenterList)
					.withArrayClaim(CLAIM_GROUP_CENTER_HIERARCHY_TREE_LIST, tokenHelper.groupCenterHierarchyTreeList)
					.withArrayClaim(CLAIM_PATIENT_SHARE_CENTER_LIST, tokenHelper.patientShareCenterList)
					.sign(buildAlgorithm());
		} catch (Exception e) {
			logger.error("Issue creating JWT", e);
		}

		return token;
	}

	private static Date createExpirationTime(int hours) {
		// Create the expiration time
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, hours); // adds one hour
		// cal.add(Calendar.MINUTE, 1);
		Date expirationTime = cal.getTime();

		return expirationTime;
	}

	private static Algorithm buildAlgorithm() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg;

		kpg = KeyPairGenerator.getInstance("RSA");

		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();

		RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();
		Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);

		return algorithm;
	}
}
