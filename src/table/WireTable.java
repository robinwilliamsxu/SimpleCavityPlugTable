/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java or Java Script plugins.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

package table;

import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.IXCavitySeal;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLibraryCavityPlug;
import com.mentor.chs.api.IXLibraryCavitySeal;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.table.IXHarnessTable;
import com.mentor.chs.plugin.table.IXTableData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple example HarnessXC table for displaying part numbers of plugs for
 * connector cavities
 * @author davewa
 */
public class WireTable implements IXHarnessTable, IXApplicationContextListener {

    /**
     * Only work on Connectors
     * @return IXConnector
     */
    private String[][] data;
    
    public Class<? extends IXObject> getTableContext() {
        return IXConnector.class;
    }

    public void initialize(IXObject ixo) {
    }

    public IXTableData populate(IXObject ixo) {
        IXConnector conn = null;
        if (ixo instanceof IXDiagramObject) {
            conn = (IXConnector) ((IXDiagramObject) ixo).getConnectivity();
        } else {
            conn = (IXConnector) ixo;
        }
        final int rownumb=conn.getPins().size();
        if (conn != null) {
            final List ConnectorWires = new ArrayList();
        
            Set<IXAbstractPin> Pins = conn.getPins();
            data = new String[500][7];

            int i = 0;
            for (IXAbstractPin Cav : Pins) 
            {
                //this code is for cavity plug
                IXCavity cavity = (IXCavity) Cav;
                if (context != null) 
                {
                    
                    for (IXAdditionalComponent ad : cavity.getAdditionalComponents()) {
                        
                        String  Spn = ad.getAttribute("SupplierPartNumber");
                        String pn= ad.getAttribute("PartNumber");
                        IXLibraryObject libObj = context.getLibrary().getLibraryObject(pn);
                        if (libObj != null && libObj instanceof IXLibraryCavityPlug) {
                           
                            data[i][0] =Cav.getAttribute("Name");
                            data[i][1] ="";
                            data[i][2] ="";
                            data[i][3] ="";
                            data[i][4] = "";
                            data[i][5] ="";
                            data[i][6] =Spn ;           
                            //data[i][7] = wire.getAttribute("OptionExpression");                   
                            i++;
                            ConnectorWires.add(Spn);   
                        }
                    }
                }
                
                //this code is for cavity Seal
               
                
                
                //this is for cavity  with wire
                if (cavity!= null) 
                {
                    final Map<IXCavity,String> cavityMap = new HashMap<IXCavity, String>();
                    if (context != null) 
                    {

                        Set<IXCavityDetail> CavityDetails= cavity.getCavityDetails();
                        for (IXCavityDetail  CavityDetail:CavityDetails) 
                        {
                            IXCavitySeal CavitySeal=CavityDetail.getSeal();
                            if(CavitySeal!=null)
                            {
                                
                                String Spn = CavitySeal.getAttribute("SupplierPartNumber");
                                String pn= CavitySeal.getAttribute("PartNumber");
                                IXLibraryObject libObj = context.getLibrary().getLibraryObject(pn);
                                if (libObj != null && libObj instanceof IXLibraryCavitySeal) {
                                    cavityMap.put(cavity,pn); 
                            }   

                            }
                        }
                    }
                    
                    Set<IXAbstractConductor> wires= Cav.getConductors();
                    for(IXAbstractConductor wire: wires)
                    {
                        if(wire!=null)
                        { 
                            
                            data[i][0] =Cav.getAttribute("Name");
                            data[i][1] =  wire.getAttribute("Name");
                            data[i][2] = wire.getAttribute("WireColor");
                            data[i][3] =wire.getAttribute("WireCSA");
                            data[i][4] =wire.getAttribute("");
                            data[i][5] =cavityMap.get(Cav);
                            data[i][6] = wire.getAttribute("");                  
                            //data[i][7] = wire.getAttribute("OptionExpression");                   
                            i++;
                            ConnectorWires.add(wire);
                        }
                       
                    }                 
                }
                
            }
            return new IXTableData() 
            {
                public int getRowCount() {
                    return ConnectorWires.size();
                }

                public Object getCellValueAt(int row, int col) {
                    Object obj = "x";
                    switch (col) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            obj = data[row][col];
                            break;
                    }
                    return obj;
                }

                public int getColumnCount() {
                   return 7;
                }

                public int getHeaderCount() {
                    return 1;
                }

                public Object getHeaderValueAt(int i, int col) {
                    Object obj = null;
                        switch (col) {
                            case 0:
                                obj = "Cav";
                                break;
                            case 1:
                                obj = "Circut No";
                                break;
                            case 2:
                                obj = "Color";
                                break;
                            case 3:
                                obj = "CSA  ";
                                break;
                            case 4:
                                obj = "OP";
                                break;
                            case 5:
                                obj = "Seal PN (supplier PN)";
                                break;
                            case 6:
                                obj = "Plug PN (Supplier PN)";
                                break;
                           }
                        return obj;
                            }
            };
        }
        return null;
    }

    public String getDescription() {
        return "Displays Plugged Connector Cavities Only";
    }

    public String getName() {
        return "Connector Cavity Plug Table";
    }

    public String getVersion() {
        return "1";
    }
    private IXApplicationContext context = null;

    public void setApplicationContext(IXApplicationContext ixac) {
        context = ixac;
    }
}
