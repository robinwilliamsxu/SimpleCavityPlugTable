/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
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
import com.mentor.chs.plugin.table.IXHarnessTable;
import com.mentor.chs.plugin.table.IXTableData;
import com.mentor.chs.api.*;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;

import java.util.Set;

public class ConnectorWireTable 
{
    private IXHarnessDesign design;
    private IXConnector conn;
    private XTableData m_xTableData;
    private String[][] data;

    public String getDescription() {
        return "Sample table plug-in for Connector Cavity ";
    }

    public String getName() {
        return "Cavity Wires Table";
    }

    public String getVersion() {
        return "1.0";
    }

    public Class getTableContext() {
        return IXConnector.class;
    }

    public void initialize(IXObject xObject) {
        
        try {
        if (xObject instanceof IXDiagramObject) 
        {
            conn = (IXConnector) ((IXDiagramObject) xObject).getConnectivity();
        } 
        else 
        {
            conn = (IXConnector) xObject;
        }
//        {
//                design = (IXHarnessDesign) xObject;
//            }
//            m_xTableData = new ConnectorWireTable.XTableData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IXTableData populate(IXObject xObject) {
        return m_xTableData;
    }

   

    class XTableData implements IXTableData {

        XTableData() 
        {
            Set<IXAbstractPin> Pins = conn.getPins();
            data = new String[getRowCount()][getColumnCount()];

            int i = 0;
            for (IXAbstractPin Pin : Pins) 
            {

                if (Pin != null) 
                {
                    Set<IXAbstractConductor> wires= Pin.getConductors();
                    for(IXAbstractConductor wire: wires)
                    {
                        if(wire!=null)
                        { 
                            data[i][0] = wire.getAttribute("Name");
                            data[i][1] = wire.getAttribute("WireColor");
                            data[i][2] = wire.getAttribute("WireCSA");
                            data[i][3] = wire.getAttribute("");
                            data[i][4] = wire.getAttribute("");
                            data[i][5] = wire.getAttribute("");
                            data[i][6] = wire.getAttribute("");                  
                            data[i][7] = wire.getAttribute("OptionExpression");                   
                            i++;
                        }
                       
                    }                 
                }
                
            }
        }

        public int getColumnCount() {
            return 7;
        }

        public int getRowCount() {
            return conn.getPins().size();
        }


        public int getHeaderCount() {
            return 1;
        }

        public Object getCellValueAt(int row, int column) {
            Object obj = "x";
            switch (column) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    obj = data[row][column];
                    break;
            }
            return obj;
        }

        public Object getHeaderValueAt(int rowIndex, int colIndex) {
            Object obj = null;
            switch (colIndex) {
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
    }
}




