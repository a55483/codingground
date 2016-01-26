/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package createbnpincidents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author cheboc
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here


        String adminName = "bnpuser";
        String adminCompany = "50000";
        String adminPwd = "Archer2011";

        String adminSession = createUserSessionFromInstance(adminName, adminCompany, adminPwd);

        HashMap transformStepNumber = new HashMap();
        transformStepNumber.put("Step 1", "12003");
        transformStepNumber.put("Step 2", "12005");
        transformStepNumber.put("Step 3", "12007");
        transformStepNumber.put("Step 4", "12009");
        transformStepNumber.put("Step 5", "12011");
        transformStepNumber.put("Step 6", "12013");
        transformStepNumber.put("Step 7", "12015");
        transformStepNumber.put("Step 8", "12017");
        transformStepNumber.put("Step 9", "12019");

        // Build the Matrix
        HashMap matrixStepCategory = buildStepMatrix(adminSession);

        // Search Incidents without Response Procedure
        String result = searchRecordsInArcher(adminSession, 357, "<Search><Display><Field id='12003'/></Display></Search>");
        ArrayList emptyIncidents = getNewIncident(result);
        HashMap incidentHashMap = retrieveIncidentCategory(adminSession, emptyIncidents);

        // Update Incidents
        updateRecords(adminSession, incidentHashMap, matrixStepCategory, transformStepNumber);


        terminateSession(adminSession);

    }

    public static HashMap buildStepMatrix(String adminSession) {
        HashMap returnedMap = new HashMap();
        String result = searchRecordsInArcher(adminSession, 137, "<Search><Display><Field id='12000'/></Display></Search>");
        returnedMap = buildValueHashMapForMatrix(adminSession, 12000, result);
        return returnedMap;
    }

    public static String searchRecordsInArcher(String adminSession, int moduleId, String searchOptions) {

        String returnedString = "";
        int searchId = searchRecords(adminSession, moduleId, searchOptions);
        int pageIterator = 0;
        boolean stayInLoop = true;

        while (stayInLoop) {
            pageIterator = pageIterator + 1;
            returnedString = retrieveSearchResultsPageByPageSize(adminSession, searchId, pageIterator, 10000);
            stayInLoop = false;
        }
        return returnedString;
    }

    public static HashMap buildValueHashMapForMatrix(String adminSession, int fieldId, String result) {
        HashMap returnedMap = new HashMap();
        if (result != null) {
            String[] splitedResult = splitedResult = split(result, "</Record>");
            if (splitedResult.length != 0) {
                for (int i = 0; i < splitedResult.length; i++) {
                    String parentRecord = splitedResult[i];
                    if (parentRecord != null) {
                        String parentTag = "<Record contentId=\"";
                        String childTag = " rowValueName=\"";
                        String endTag = "\"";
                        String idParent = "";
                        String childName = "";
                        String endTagChild = "\" columnValueColor=\"";
                        String endTagChild2 = "\" columnValueName=\"";

                        int pos1 = parentRecord.indexOf(parentTag);
                        if (pos1 != -1) {
                            parentRecord = parentRecord.substring(pos1 + parentTag.length(), parentRecord.length());
                            int pos2 = parentRecord.indexOf(endTag);
                            idParent = parentRecord.substring(0, pos2);
                            parentRecord = parentRecord.substring(pos2, parentRecord.length());
                            if (parentRecord != null) {
                                boolean hasChild = true;
                                ArrayList childList = new ArrayList();
                                while (hasChild) {
                                    HashMap childHash = new HashMap();
                                    int pos3 = parentRecord.indexOf(childTag);
                                    if (pos3 != -1) {
                                        parentRecord = parentRecord.substring(pos3 + childTag.length(), parentRecord.length());
                                        int pos4 = parentRecord.indexOf(endTagChild);
                                        childName = parentRecord.substring(0, pos4);

                                        int pos5 = childName.indexOf(endTag);
                                        String category = childName.substring(0, pos5);
                                        int pos6 = childName.indexOf(endTagChild2);
                                        String step = childName.substring(pos6 + endTagChild2.length());
                                        if (true) {
                                            childHash.put(category, step);
                                        }
                                        parentRecord = parentRecord.substring(pos4, parentRecord.length());

                                        if (returnedMap.get(category) == null) {
                                            HashMap newCategoryHashMap = new HashMap();
                                            newCategoryHashMap.put(step, idParent);
                                            returnedMap.put(category, newCategoryHashMap);
                                        } else {
                                            HashMap oldCategoryHashMap = (HashMap) returnedMap.get(category);
                                            if (oldCategoryHashMap.get(step) == null) {
                                                oldCategoryHashMap.put(step, idParent);
                                            }
                                            returnedMap.put(category, oldCategoryHashMap);
                                        }
                                    } else {
                                        hasChild = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return returnedMap;
    }

    public static String[] split(String chaine, String separator) {
        if (chaine != null) {
            Vector vector = new Vector(0);
            if (separator != null) {
                boolean finished = false;
                String currentChaine = chaine;

                while (!finished) {
                    int pos = currentChaine.indexOf(separator);
                    if (pos == 0) {
                        // start with separator
                        vector.add("");
                        currentChaine = currentChaine.substring(separator.length(), currentChaine.length());
                    } else if (pos == -1) {
                        // last field
                        vector.add(currentChaine);
                        currentChaine = "";
                        finished = true;
                    } else {
                        // classic case
                        vector.add(currentChaine.substring(0, pos));
                        currentChaine = currentChaine.substring(pos + separator.length(), currentChaine.length());
                    }
                }
            } else {
                vector.add(chaine);
            }
            return (String[]) vector.toArray(new String[vector.size()]);
        } else {
            return null;
        }
    }

    public static ArrayList getNewIncident(String result) {
        ArrayList returnedList = new ArrayList();
        if (result != null) {
            String[] splitedResult = splitedResult = split(result, "</Record>");
            if (splitedResult.length != 0) {
                for (int i = 0; i < splitedResult.length; i++) {
                    String parentRecord = splitedResult[i];
                    if (parentRecord != null) {
                        String parentTag = "<Record contentId=\"";
                        String childTag = "<Record id=\"";
                        String endTag = "\"";
                        String idParent = "";

                        int pos1 = parentRecord.indexOf(parentTag);
                        if (pos1 != -1) {
                            parentRecord = parentRecord.substring(pos1 + parentTag.length(), parentRecord.length());
                            int pos2 = parentRecord.indexOf(endTag);
                            idParent = parentRecord.substring(0, pos2);
                            parentRecord = parentRecord.substring(pos2, parentRecord.length());

                            if (parentRecord != null) {
                                int pos4 = parentRecord.indexOf(childTag);
                                if (pos4 == -1) {
                                    returnedList.add(idParent);
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnedList;
    }

    private static HashMap retrieveIncidentCategory(String adminSession, ArrayList emptyIncidents) {
        HashMap returnedMap = new HashMap();
        if (emptyIncidents != null) {
            Iterator itIncident = emptyIncidents.iterator();
            while (itIncident.hasNext()) {
                String idIncident = (String) itIncident.next();
                int incidentId = new Integer(idIncident);
                String record = getRecordById(adminSession, 357, incidentId);
                String bal1 = "value=\"";

                if (record != null) {
                    int pos0 = record.indexOf("Field id=\"12059\"");
                    record = record.substring(pos0);

                    int pos1 = record.indexOf(bal1);
                    record = record.substring(pos1 + bal1.length());

                    int pos2 = record.indexOf("\"");
                    record = record.substring(0, pos2);

                    returnedMap.put(incidentId + "", record);
                }
            }
        }
        return returnedMap;
    }

    public static List getKeys(Map map) {
        if (map != null) {
            return new ArrayList(map.keySet());
        }
        return new ArrayList();
    }

    private static void updateRecords(String adminSession, HashMap incidentHashMap, HashMap matrixStepCategory, HashMap transformStepNumber) {
        if (incidentHashMap != null) {
            ArrayList incidentList = (ArrayList) getKeys(incidentHashMap);
            if (incidentList != null) {
                Iterator itIncident = incidentList.iterator();
                while (itIncident.hasNext()) {
                    String incidentId = (String) itIncident.next();
                    String incidentCat = (String) incidentHashMap.get(incidentId);
                    HashMap stepMap = (HashMap) matrixStepCategory.get(incidentCat);
                    int idIncident = new Integer(incidentId);
                    String xmlRecord = "<Fields>";

                    ArrayList stepList = (ArrayList) getKeys(stepMap);

                    Iterator it = stepList.iterator();
                    while (it.hasNext()) {
                        String stepSZ = (String) it.next();
                        String stepID = (String) transformStepNumber.get(stepSZ);
                        xmlRecord = xmlRecord + "<Field id=\"" + stepID + "\" type=\"9\">";
                        xmlRecord = xmlRecord + "<MultiValue value=\"" + stepMap.get(stepSZ) + "\"/>";
                        xmlRecord = xmlRecord + "</Field>";
                    }
                    xmlRecord = xmlRecord + "</Fields>";
                    updateRecord(adminSession, 357, idIncident, xmlRecord);
                }
            }
        }
    }

    private static String createUserSessionFromInstance(java.lang.String userName, java.lang.String instanceName, java.lang.String password) {
        com.archer_tech.webservices.General service = new com.archer_tech.webservices.General();
        com.archer_tech.webservices.GeneralSoap port = service.getGeneralSoap();
        return port.createUserSessionFromInstance(userName, instanceName, password);
    }

    private static int terminateSession(java.lang.String sessionToken) {
        com.archer_tech.webservices.General service = new com.archer_tech.webservices.General();
        com.archer_tech.webservices.GeneralSoap port = service.getGeneralSoap12();
        return port.terminateSession(sessionToken);
    }

    private static int searchRecords(java.lang.String sessionToken, int moduleId, java.lang.String searchOptions) {
        com.archer_tech.webservices.Search service = new com.archer_tech.webservices.Search();
        com.archer_tech.webservices.SearchSoap port = service.getSearchSoap();
        return port.searchRecords(sessionToken, moduleId, searchOptions);
    }

    private static String retrieveSearchResultsPageByPageSize(java.lang.String sessionToken, int searchId, int pageId, int recordsPerPage) {
        com.archer_tech.webservices.Search service = new com.archer_tech.webservices.Search();
        com.archer_tech.webservices.SearchSoap port = service.getSearchSoap();
        return port.retrieveSearchResultsPageByPageSize(sessionToken, searchId, pageId, recordsPerPage);
    }

    private static String getRecordById(java.lang.String sessionToken, int moduleId, int contentId) {
        com.archer_tech.webservices.Record service = new com.archer_tech.webservices.Record();
        com.archer_tech.webservices.RecordSoap port = service.getRecordSoap();
        return port.getRecordById(sessionToken, moduleId, contentId);
    }

    private static int updateRecord(java.lang.String sessionToken, int moduleId, int contentId, java.lang.String fieldValues) {
        com.archer_tech.webservices.Record service = new com.archer_tech.webservices.Record();
        com.archer_tech.webservices.RecordSoap port = service.getRecordSoap12();
        return port.updateRecord(sessionToken, moduleId, contentId, fieldValues);
    }
}
