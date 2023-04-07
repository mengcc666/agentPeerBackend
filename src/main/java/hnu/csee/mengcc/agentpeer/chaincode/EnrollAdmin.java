/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package hnu.csee.mengcc.agentpeer.chaincode;

import java.nio.file.Paths;
import java.util.Properties;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class EnrollAdmin {

  public static void main(String[] args) throws Exception {
    try {
      // Create a CA client for interacting with the CA.
      Properties props = new Properties();
      props.put(
        "pemFile",
        "/root/workspace/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem"
      );
      props.put("allowAllHostNames", "true");
      try {
        Class.forName("org.hyperledger.fabric.sdk.helper.Utils");
        System.out.println(
          "Class org.hyperledger.fabric.sdk.helper.Utils loaded successfully"
        );
      } catch (ClassNotFoundException e) {
        System.err.println(
          "Error loading class org.hyperledger.fabric.sdk.helper.Utils"
        );
        e.printStackTrace();
      }
      HFCAClient caClient = HFCAClient.createNewInstance(
        "https://localhost:7054",
        props
      );
      CryptoSuite cryptoSuite = CryptoSuiteFactory
        .getDefault()
        .getCryptoSuite();
      caClient.setCryptoSuite(cryptoSuite);

      // Create a wallet for managing identities
      Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

      // Check to see if we've already enrolled the admin user.
      if (wallet.get("admin") != null) {
        System.out.println(
          "An identity for the admin user \"admin\" already exists in the wallet"
        );
        return;
      }

      // Enroll the admin user, and import the new identity into the wallet.
      final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
      enrollmentRequestTLS.addHost("localhost");
      enrollmentRequestTLS.setProfile("tls");
      Enrollment enrollment = caClient.enroll(
        "admin",
        "adminpw",
        enrollmentRequestTLS
      );
      Identity user = Identities.newX509Identity("Org1MSP", enrollment);
      wallet.put("admin", user);
      System.out.println(
        "Successfully enrolled user \"admin\" and imported it into the wallet"
      );
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
