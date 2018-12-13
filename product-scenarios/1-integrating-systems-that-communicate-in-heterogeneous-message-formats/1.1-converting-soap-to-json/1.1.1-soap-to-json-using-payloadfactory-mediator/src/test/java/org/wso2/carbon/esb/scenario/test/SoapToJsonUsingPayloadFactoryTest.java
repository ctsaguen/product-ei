/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb.scenario.test;

import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.esb.scenario.test.common.HttpConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioConstants;
import org.wso2.carbon.esb.scenario.test.common.ScenarioTestBase;
import org.wso2.carbon.esb.scenario.test.common.StringUtil;
import org.wso2.esb.integration.common.utils.clients.SimpleHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This test class is to test SOAP to JSON Transformation using Payload Factory Mediator. Once a SOAP request is sent
 * to API and it will transform the SOAP Request to JSON using Payload Factory Mediator and send to backend
 * service. Backend Service will respond with JSON response and API will transform tha JSON Response to SOAP.
 */
public class SoapToJsonUsingPayloadFactoryTest extends ScenarioTestBase {

    private String cappNameWithVersion = "approach_1_1_1_synapse_configCompositeApplication_1.0.0";
    private String cappName = "approach_1_1_1_synapse_configCompositeApplication";
    private String apiName = "1_1_1_soap_to_json_using_payload_factory";
    private String apiInvocationUrl;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        apiInvocationUrl = getApiInvocationURLHttp(apiName);
        deployCarbonApplication(cappNameWithVersion);
    }

    @Test(description = "1.1.1.1-Valid Soap To Json transformation Using Payload Factory Mediator", enabled = true,
          dataProvider = "1.1.1.1")
    public void convertValidSoapToJsonUsingPayloadFactory(String request, String expectedResponse, String header)
            throws Exception {
        log.info("apiInvocationUrl is set as : " + apiInvocationUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(apiInvocationUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        log.info("Actual response received 1.1.1.1: " + responsePayload);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200, "SOAP to JSON transformation failed");

        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @Test(description = "1.1.1.2-Malformed Soap to Json Transformation Using Payload Factory Mediator", enabled = true,
          dataProvider = "1.1.1.2")
    public void convertMalformedSoapToJsonUsingPayloadfactory(String request, String expectedResponse, String header)
            throws Exception {
        log.info("proxyServiceUrl is set as : " + apiInvocationUrl);

        SimpleHttpClient httpClient = new SimpleHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(ScenarioConstants.MESSAGE_ID, header);
        HttpResponse httpResponse = httpClient.doPost(apiInvocationUrl, headers, request,
                                                      HttpConstants.MEDIA_TYPE_APPLICATION_XML);
        String responsePayload = httpClient.getResponsePayload(httpResponse);

        Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500, "SOAP to JSON transformation failed");

        log.info("Actual response received 1.1.1.2: " + responsePayload);

        Assert.assertEquals(StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(expectedResponse),
                            StringUtil.trimTabsSpaceNewLinesBetweenXMLTags(responsePayload),
                            "Actual Response and Expected Response mismatch!");
    }

    @AfterClass(description = "Server Cleanup", alwaysRun = true)
    public void cleanup() throws Exception {
    }

    @DataProvider(name = "1.1.1.1")
    public Iterator<Object[]> soapToJson_1_1_1_1() throws Exception {
        String testCase = "1.1.1.1";
        return getRequestResponseHeaderList(testCase).iterator();
    }

    @DataProvider(name = "1.1.1.2")
    public Iterator<Object[]> soapToJson_1_1_1_2() throws Exception {
        String testCase = "1.1.1.2";
        return getRequestResponseHeaderList(testCase).iterator();
    }
}
