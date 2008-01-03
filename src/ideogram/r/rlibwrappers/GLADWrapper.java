/*
 * File:	GLADWrapper.java
 * Created: 08.12.2007
 * Author:	Ferdinand Hofherr <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r.rlibwrappers;

import ideogram.r.RController;
import ideogram.r.RDataSetWrapper;
import ideogram.r.RException;
import ideogram.r.annotations.Analysis;
import ideogram.r.annotations.RBoolParam;
import ideogram.r.annotations.RDsNameParam;
import ideogram.r.annotations.RNumericParam;
import ideogram.r.annotations.RStringParam;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.REXP;

/**
 * INSERT DOCUMENTATION HERE!
 *
 * @author Ferdinand Hofherr
 *
 */
public class GLADWrapper extends AbstractRWrapper {

    private static final String LIB_NAME = "GLAD";
    private static final String PROFILE_CGH_VARNAME = "profileCGH";
    private static final String ALGO_RES_VARNAME = "res";
    
    private List<RDataSetWrapper> sampleData;
    
    // Parameters for glad function.
 
    @Analysis("glad")
    @RDsNameParam(name = "Data set", mandatory = true)
    public String gladDataSet; 
  
    @Analysis("glad")
    @RBoolParam(name = "mediancenter", mandatory = false)
    public String gladMediancenter;
    
    @Analysis("glad")
    @RBoolParam(name = "base", mandatory = false) 
    public String gladBase;
    
    @Analysis("glad")
    @RBoolParam(name = "verbose", mandatory = false)
    public String gladVerbose;

    @Analysis("glad")
    @RStringParam(name = "smoothfunc", mandatory = false)
    public String gladSmoothfunc; 
   
    @Analysis("glad")
    @RStringParam(name = "model", mandatory = false)
    public String gladModel;
    
    @Analysis("glad")
    @RStringParam(name = "lkern", mandatory = false)
    public String gladLkern;
    
    @Analysis("glad")
    @RStringParam(name = "type", mandatory = false) 
    public String gladType;
    
    @Analysis("glad")
    @RStringParam(name = "method", mandatory = false) 
    public String gladMethod;

    
    @Analysis("glad")
    @RNumericParam(name = "bandwidth", mandatory = false) 
    public double gladBandwidth;
    
    @Analysis("glad")
    @RNumericParam(name = "round", mandatory = false) 
    public double gladRound;
    
    @Analysis("glad")
    @RNumericParam(name = "qlambda", mandatory = false) 
    public double gladQlambda;
    
    @Analysis("glad")
    @RNumericParam(name = "lambdabreak", mandatory = false) 
    public double gladLambdabreak;
    
    @Analysis("glad")
    @RNumericParam(name = "lambdacluster", mandatory = false) 
    public double gladLambdacluster;
    
    @Analysis("glad")
    @RNumericParam(name = "lambdaClusterGen", mandatory = false) 
    public double gladLambdaClusterGen;
    
    @Analysis("glad")
    @RNumericParam(name = "param", mandatory = false) 
    public double gladParam;
    
    @Analysis("glad")
    @RNumericParam(name = "alpha", mandatory = false) 
    public double gladAlpha;
    
    @Analysis("glad")
    @RNumericParam(name = "msize", mandatory = false) 
    public double gladMsize;
    
    @Analysis("glad")
    @RNumericParam(name = "nmax", mandatory = false) 
    public double gladNmax;
    
    public GLADWrapper() {
        sampleData = new ArrayList<RDataSetWrapper>();
    
        // set default values for glad function. If null, no default value available
        gladDataSet = null;
        gladMediancenter = "FALSE";
        gladSmoothfunc = "lawsglad";
        gladBandwidth = 10;
        gladRound = 1.5;
        gladModel = "Gaussian";
        gladLkern = "Exponential";
        gladQlambda = 0.999;
        gladBase = "FALSE";
        gladLambdabreak = 8;
        gladLambdacluster = 8;
        gladLambdaClusterGen = 40;
        gladType = "tricubic";
        gladParam = 6;
        gladAlpha = 0.001;
        gladMsize = 5;
        gladMethod = "centroid";
        gladNmax = 8;
        gladVerbose = "FALSE";
        
        // Add the names of the available sample data.
        RDataSetWrapper dw;
        dw = new RDataSetWrapper("veltman", "Public CGH data of Veltman");
        dw.addElement("P20");
        dw.addElement("P9");
        sampleData.add(dw);
        
        dw = new RDataSetWrapper("arrayCGH", "Bladder cancer CGH data");
        dw.addElement("array1");
        dw.addElement("array2");
        dw.addElement("array3");
        sampleData.add(dw);
        
        dw = new RDataSetWrapper("cytoband", "Cytogenetic banding");
        dw.addElement("cytoband");
        sampleData.add(dw);
        
        dw = new RDataSetWrapper("snijders", "Public CGH data of Snijders");
        dw.addElement("gm00143");
        dw.addElement("gm01524");
        dw.addElement("gm01535");
        dw.addElement("gm01750");
        dw.addElement("gm02948");
        dw.addElement("gm03134");
        dw.addElement("gm03563");
        dw.addElement("gm03576");
        dw.addElement("gm04435");
        dw.addElement("gm05296");
        dw.addElement("gm07081");
        dw.addElement("gm07408");
        dw.addElement("gm10315");
        dw.addElement("gm13031");
        dw.addElement("gm13330");
        sampleData.add(dw);
    }
    
    /* Methods required by ideogram.r.RLibrary and ideogram.r.AbstractRWrapper */
    
    /* (non-Javadoc)
     * @see ideogram.r.AbstractRWrapper#hasSampleData()
     */
    @Override
    public boolean hasSampleData() {
        return true;
    }

    /* (non-Javadoc)
     * @see ideogram.r.AbstractRWrapper#listSampleData()
     */
    @Override
    public List<RDataSetWrapper> listSampleData() {
        return sampleData;
    }

    /* (non-Javadoc)
     * @see ideogram.r.AbstractRWrapper#load()
     */
    @Override
    public void loadLibrary() throws RException {
        RController.getInstance().loadRLibrary(LIB_NAME);
    }

    /* (non-Javadoc)
     * @see ideogram.r.AbstractRWrapper#unload()
     */
    @Override
    public void unloadLibrary() throws RException {
        RController.getInstance().unloadRLibrary(LIB_NAME);
    }

    /* (non-Javadoc)
     * @see ideogram.r.RLibrary#loadSampleData(ideogram.r.RDataSetWrapper)
     */
    public void loadSampleData(RDataSetWrapper data) throws RException {
        RController.getInstance().loadDataSet(data.getName());
    }

    /* (non-Javadoc)
     * @see ideogram.r.AbstractRWrapper#getResult()
     */
    @Override
    public REXP getResult() throws RException {
        RController rc = RController.getInstance();
        REXP res = null;
        if (rc.engineRunning()) {
            res = rc.getEngine().eval(ALGO_RES_VARNAME);
            //rc.getEngine().eval("print("+ALGO_RES_VARNAME+")");
        }
        else {
            throw new RException("R not running!");
        }
        return res;
    }
    
    /* Other methods, which will be discovered via reflection */
        
    @Analysis("glad")
    public REXP useGladFunction() throws RException{
        RController rc = RController.getInstance();
        
        if (!rc.engineRunning()) {
            throw new RException("R not running!");
        }
        if (gladDataSet == null || gladDataSet.equals("")) {
            throw new RException("No data set specified!");
        }
        
        //createProfileCGH(dsName);
        String wrappedParam = "c(d = "+ gladParam +")";
        REXP res = rc.getEngine().eval(ALGO_RES_VARNAME + 
                " <- glad(as.profileCGH(" + gladDataSet + ")" +
                ", mediancenter = " + gladMediancenter + 
                ", smoothfunc = '" + gladSmoothfunc + "'" +
                ", bandwidth = " + gladBandwidth +
                ", round = " + gladRound +
                ", model = '" + gladModel + "'" +
                ", lkern = '" + gladLkern + "'" +
                ", qlambda = " + gladQlambda +
                ", base = " + gladBase + 
                ", lambdabreak = " + gladLambdabreak + 
                ", lambdacluster = " + gladLambdacluster +
                ", lambdaClusterGen = " + gladLambdaClusterGen + 
                ", type = '" + gladType + "'" +
                ", param = " + wrappedParam + 
                ", alpha = " + gladAlpha +
                ", msize = " + gladMsize +
                ", method = '" + gladMethod + "'" +
                ", nmax = " + gladNmax +
                ", verbose = " + gladVerbose +
        ")");
        if (res == null) {
            throw new RException("Function glad() returned with null!");
        }
        return res;
    }
    
    
}
