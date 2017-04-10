/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csiottrust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author leonid
 */
public class CSIoTTrust {

    private static final String XFILENAME = "Xis";
    private static final String LFILENAME = "Lis";
    private static final String PFILENAME = "Pis";
    private static final String SFILENAME = "Samples";
    private static final String UFILENAME = "users";
    private static final String QFILENAME = "Qis";
    private static final String NQFILENAME = "NQis";
    private static final String SVFILENAME = "sampleVoters";
    private static final String PACAPFILENAME = "PACAP";
    private static final String PPRIMEFILENAME = "PPRIMEis";
    private static final String RFILENAME = "Rjs";
    private static final String BFILENAME = "bins";
    
    
    

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO code application logic here
        System.out.println("Hello World!"); // Display the string.
        long version=System.currentTimeMillis();
        int maximum=100;
        int minimum=1;
        int randomNum=0;
        int DATA_SIZE=10000;
        int USER_SIZE=50000;
        double mean=100;
        double groundTruth=mean;
        double dev=1;
        double binLength=0.19;
        int numberOfBins=0; // initialize later in code
        int T0=3600;
        int tauw=600;
      
        double mean2=0;
        double dev2=1; 
        double averagePunctuality=1;
        double threshold=binLength*2;
        
        double[] X= new double[DATA_SIZE];
        double[] Ux= new double[DATA_SIZE];   
        double[] V= new double[DATA_SIZE];
        double[] P= new double[DATA_SIZE];
        double[] pPrimeR= new double[DATA_SIZE];
        double[] pPrimeP= new double[DATA_SIZE];
        double[] pPrimeO= new double[DATA_SIZE];
        
        
        int[] K= new int[DATA_SIZE];
        
        
        double[] L= new double[DATA_SIZE];
        double[] pOld= new double[DATA_SIZE];
        double epsilon=0.25;
        double sigma=0.25;
        double etha=1;
        double l=1;
        double[] U= new double[USER_SIZE];
        double[] Q= new double[USER_SIZE];
        double[] NQ= new double[USER_SIZE];
        double[] T= new double[USER_SIZE];
        double[] R= new double[USER_SIZE];
        double[] Landa= new double[USER_SIZE];
        boolean[] visitedUser= new boolean[USER_SIZE];
        java.util.Arrays.fill(visitedUser,0,USER_SIZE-1,false);
        
        Arrays.fill(Landa,1);
        
        
        
        int M=20;
        int MV=40000; 
        int mMin=(int)(MV*0.9);
        ArrayList<vote> offers=new ArrayList<vote>();
        int wl=1;

        
        double[] sampleData= new double[M];
        double[] UxOfSamples= new double[M]; 
        int[] sampleVoters= new int[MV];
        int[][] votes= new int[M][MV];
        
        

        
        randomNum = minimum + (int)(Math.random() * maximum);
        System.out.println(randomNum); // Display the string.
        
        //generate the data
            try(FileWriter fw = new FileWriter(XFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                
                for(int i=0;i<DATA_SIZE;i++){
                    Random r = new Random();
                    X[i]=r.nextGaussian()*dev+mean;
                    System.out.println("X["+i+"] is: "+X[i]);
                    out.println(i+","+X[i]);
                }
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            
        //generate the bins
            try(FileWriter fw = new FileWriter(BFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                double min=X[0];
                double max=X[0];
                double[] temp= new double[DATA_SIZE];
                int count=0;

                for(int i=0;i<DATA_SIZE;i++){
                    if(X[i]>max) max=X[i];
                    if(X[i]<min) min=X[i];
                }
                System.out.println("min is: "+min);
                System.out.println("max is: "+max);
                double currBinStartPtr=min;
                double nextBinStartPtr=min+binLength; 
                numberOfBins=(int)ceil((max-min)/binLength);

                for(int j=0;j<numberOfBins;j++){
                    count=0;
                    for(int i=0;i<DATA_SIZE;i++){                                   

                        if(X[i]>=currBinStartPtr && X[i]<nextBinStartPtr) { temp[count]=X[i]; count++; }

                    }
                    if(count!=0){
                        P[j]=((double)count)/DATA_SIZE;
                        K[j]=count;
                        V[j]=((nextBinStartPtr-currBinStartPtr)/2)+currBinStartPtr;;
                    }
                    else {P[j]=0; V[j]=((nextBinStartPtr-currBinStartPtr)/2)+currBinStartPtr;}
                    
                    currBinStartPtr=nextBinStartPtr;
                    nextBinStartPtr+=binLength;
                    out.println(V[j]+","+P[j]);
                }
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            
            
            //generate one sample 

/*
                double rand;
                double[] linePartitions= new double[numberOfBins+1];
                double pSample=-1;
                double oppSample=-1;
                double rSample=-1;
                
                //p sample
                linePartitions[0]=0;
                for(int i=0;i<numberOfBins;i++){
                    linePartitions[i+1]=P[i]+linePartitions[i];
                }
                
                rand=Math.random();
                for(int i=0;i<numberOfBins-1;i++){
                    if(rand>=linePartitions[i] && rand<linePartitions[i+1]){
                       pSample=V[i]; break;
                    }
                }
                if(rand>=linePartitions[numberOfBins-1]){
                        pSample=V[numberOfBins-1]; 
                    }
                System.out.println("P sample is: "+pSample);

                //1-p sample
                linePartitions[0]=0;
                for(int i=0;i<numberOfBins;i++){
                    linePartitions[i+1]=(1-P[i])+linePartitions[i];
                }
                
                rand=Math.random();
                for(int i=0;i<numberOfBins-1;i++){
                    if(rand>=linePartitions[i] && rand<linePartitions[i+1]){
                       oppSample=V[i]; break;
                    }
                }
                if(rand>=linePartitions[numberOfBins-1]){
                        oppSample=V[numberOfBins-1]; 
                    }
                System.out.println("1-P sample is: "+oppSample);
                
                //random sample
                linePartitions[0]=0;
                for(int i=0;i<numberOfBins;i++){
                    linePartitions[i+1]=(1/(double)numberOfBins)+linePartitions[i];
                }
                
                rand=Math.random();
                for(int i=0;i<numberOfBins-1;i++){
                    if(rand>=linePartitions[i] && rand<linePartitions[i+1]){
                       rSample=V[i]; break;
                    }
                }
                if(rand>=linePartitions[numberOfBins-1]){
                        rSample=V[numberOfBins-1]; 
                    }
                System.out.println("random sample is: "+rSample);
          
   */     
            
            
            
            
            
            
            
            
            
            
               
            
            
        //generate the users 
            try(FileWriter fw = new FileWriter(UFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                for(int i=0;i<USER_SIZE;i++){
                    Random r = new Random();
                    U[i]=r.nextGaussian()*dev2+mean2;
        //            System.out.println("U["+i+"] is: "+U[i]);
                   // out.println(i+","+U[i]);
                }
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }            
            
            //generate the Likelyhoods of getting sampled for users
            try(FileWriter fw = new FileWriter(QFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                long now=System.currentTimeMillis();
                for(int j=0;j<USER_SIZE;j++){
                    Q[j]=1-Math.exp(-Landa[j]*(now-T[j])*(R[j]+epsilon));
  //                  System.out.println("Q["+j+"] is: "+Q[j]);
                    out.println(j+","+Q[j]);
                }
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }            
            
            //generate the Probabilities of getting sampled for users
            try(FileWriter fw = new FileWriter(NQFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                double total=0;
                for(int j=0;j<USER_SIZE;j++){
                    total+=Q[j];
                }
                
                for(int j=0;j<USER_SIZE;j++){
                    NQ[j]=Q[j]/total;
  //                  System.out.println("NQ["+j+"] is: "+NQ[j]);
                    out.println(j+","+NQ[j]);
                }                
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }            
            
/*            
            //generate the sample voters
            try(FileWriter fw = new FileWriter(SVFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {

                int C=0;
                double x;
                boolean[] visited= new boolean[USER_SIZE];
                double[] lineSegments= new double[USER_SIZE+1];

                lineSegments[0]=0;
                java.util.Arrays.fill(visited,0,USER_SIZE-1,false);
                for(int i=0;i<USER_SIZE;i++){
                    lineSegments[i+1]=NQ[i]+lineSegments[i];
                }
                while(C<MV){
                    x=Math.random();
                    for(int i=0;i<USER_SIZE-1;i++){
                        if(x>=lineSegments[i] && x<lineSegments[i+1]){
                            if(visited[i]) break; else {visited[i]=true; sampleVoters[C]=i; C++; break;}
                        }
                    }
                    if(x>=lineSegments[USER_SIZE-1]){
                            if(!visited[USER_SIZE-1]) {visited[USER_SIZE-1]=true; sampleVoters[C]=USER_SIZE-1; C++;}
                        }
                }
                
                for(int i=0;i<MV;i++){
                    System.out.println("SampleVoter["+i+"] is: "+sampleVoters[i]);

                }                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }            
      */           
            
            //run PACAP
            try(FileWriter fw = new FileWriter(PACAPFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
               double maliciousity=dev2*1000;
               double ignorranceDegree=0.1;
       
               
               int[] m=new int[(T0/tauw)+1];
               m[0]=MV;
               int mN=0;
               int mY=0;    
               
               for(int k=0;k<T0/tauw;k++){
                

                    //generate sample voters begin 

                    int C=0;
                    double x;
                    double[] lineSegments= new double[USER_SIZE+1];

                    lineSegments[0]=0;
                    for(int i=0;i<USER_SIZE;i++){
                        lineSegments[i+1]=NQ[i]+lineSegments[i];
                    }
                    while(C<m[k]){
                        x=Math.random();
                        for(int i=0;i<USER_SIZE-1;i++){
                            
                            if(x>=lineSegments[i] && x<lineSegments[i+1]){
                                if(visitedUser[i]) break; else {visitedUser[i]=true; sampleVoters[C]=i; C++; break;}
                            }
                        }
                        if(x>=lineSegments[USER_SIZE-1]){
                                if(!visitedUser[USER_SIZE-1]) {visitedUser[USER_SIZE-1]=true; sampleVoters[C]=USER_SIZE-1; C++;}
                            }
                    }

                    for(int i=0;i<m[k];i++){
                        System.out.println("SampleVoter["+i+"] is: "+sampleVoters[i]);

                    }    
                    //generate sample voters end   


                    //sample and push
                    for(int j=0;j<m[k];j++){  
                        //generate one sample 
                        double rand;
                        double[] linePartitions= new double[numberOfBins+1];
                        double pSample=-1;
                        double oppSample=-1;
                        double rSample=-1;

                        //p sample
                        linePartitions[0]=0;
                        for(int i=0;i<numberOfBins;i++){
                            linePartitions[i+1]=P[i]+linePartitions[i];
                        }

                        rand=Math.random();
                        for(int i=0;i<numberOfBins-1;i++){
                            if(rand>=linePartitions[i] && rand<linePartitions[i+1]){
                               pSample=V[i]; break;
                            }
                        }
                        if(rand>=linePartitions[numberOfBins-1]){
                                pSample=V[numberOfBins-1]; 
                            }
                        System.out.println("P sample is: "+pSample);

                        //1-p sample
                        linePartitions[0]=0;
                        for(int i=0;i<numberOfBins;i++){
                            linePartitions[i+1]=((1-P[i])/(numberOfBins-1))+linePartitions[i];
                        }

                        rand=Math.random();
                        for(int i=0;i<numberOfBins-1;i++){
                            if(rand>=linePartitions[i] && rand<linePartitions[i+1]){
                               oppSample=V[i]; break;
                            }
                        }
                        if(rand>=linePartitions[numberOfBins-1]){
                                oppSample=V[numberOfBins-1]; 
                            }
                        System.out.println("1-P sample is: "+oppSample);

                        //random sample
                        linePartitions[0]=0;
                        for(int i=0;i<numberOfBins;i++){
                            linePartitions[i+1]=(1/(double)numberOfBins)+linePartitions[i];
                        }

                        rand=Math.random();
                        for(int i=0;i<numberOfBins-1;i++){
                            if(rand>=linePartitions[i] && rand<linePartitions[i+1]){
                               rSample=V[i]; break;
                            }
                        }
                        if(rand>=linePartitions[numberOfBins-1]){
                                rSample=V[numberOfBins-1]; 
                            }
                        System.out.println("random sample is: "+rSample);
                   
                   
                        //generate one sample end

                        //push one sample of each kind
                        vote temp=new vote();
                        temp.voter=sampleVoters[j];
                        double tempRand=Math.random();
                        if(U[sampleVoters[j]]> maliciousity) mY++;
                        else if(tempRand>ignorranceDegree) mY++;
                        else mN++;
                        //push pSample
                        temp.votedP=pSample;
                        if(abs(pSample-groundTruth)<= threshold){
                                     if(U[sampleVoters[j]]> maliciousity) temp.voteP=-1; else { if(tempRand>ignorranceDegree) temp.voteP=1; else temp.voteP=0; }
                                     System.out.println("User["+j+"] voted "+temp.voteP+" for sampleData V(i) "+pSample);

                        }
                        else {
                                     if(U[sampleVoters[j]]> maliciousity) temp.voteP=1; else { if(tempRand>ignorranceDegree) temp.voteP=-1; else temp.voteP=0; }
                                     System.out.println("User["+j+"] voted "+temp.voteP+" for sampleData V(i) "+pSample);

                             }                       
                        //push pSample End
                        
                        //push oppSample
                        temp.votedOpp=oppSample;
                        if(abs(oppSample-groundTruth)<= threshold){
                                     if(U[sampleVoters[j]]> maliciousity) temp.voteOpp=-1; else { if(tempRand>ignorranceDegree) temp.voteOpp=1; else temp.voteOpp=0; }
                                     System.out.println("User["+j+"] voted "+temp.voteOpp+" for sampleData V(i) "+oppSample);

                        }
                        else {
                                     if(U[sampleVoters[j]]> maliciousity) temp.voteOpp=1; else { if(tempRand>ignorranceDegree) temp.voteOpp=-1; else temp.voteOpp=0; }
                                     System.out.println("User["+j+"] voted "+temp.voteOpp+" for sampleData V(i) "+oppSample);

                             }                       
                        //push oppSample End 
                        
                        //push RSample
                        temp.votedR=rSample;
                        if(abs(rSample-groundTruth)<= threshold){
                                     if(U[sampleVoters[j]]> maliciousity) temp.voteR=-1; else { if(tempRand>ignorranceDegree) temp.voteR=1; else temp.voteR=0; }
                                     System.out.println("User["+j+"] voted "+temp.voteR+" for sampleData V(i) "+rSample);

                        }
                        else {
                                     if(U[sampleVoters[j]]> maliciousity) temp.voteR=1; else { if(tempRand>ignorranceDegree) temp.voteR=-1; else temp.voteR=0; }
                                     System.out.println("User["+j+"] voted "+temp.voteR+" for sampleData V(i) "+rSample);

                             }                       
                        //push RSample End                        
                        offers.add(temp);
                        //push one sample of each kind end
                        

                    }
                    
                    
                    
                    //sample and push end

               
               
               
               
               
               
               
               
               
               
               if(mY>=MV) {System.out.println("Success!"); break;}
               m[k+1]=(int)((MV-mY)*(1+((double)mN/mY)));
               System.out.println("M["+(k+1)+"] is : "+m[k+1]);
               
               }
               if(mY>=mMin && mY<MV) {System.out.println("Success!");}               
               else if(mY<mMin) System.out.println("Fail!");
               System.out.println("|R| is : "+mY); 
              
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }             
            
            
            //generate the new distributions
            try(FileWriter fw = new FileWriter(PPRIMEFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
               out.println("V(i),Original,Random,Proportional,Complementary");
                
               double senarioRandomGi=0;
               double senarioRandomBi=0;
               
               double senarioPGi=0;
               double senarioPBi=0;

               double senarioOppGi=0;
               double senarioOppBi=0;
               double sigmaPPrimeR=0;
               double sigmaPPrimeP=0;
               double sigmaPPrimeO=0;
               for(int i=0;i<numberOfBins;i++){
                    senarioRandomGi=0;
                    senarioRandomBi=0;
                    senarioPGi=0;
                    senarioPBi=0;
                    senarioOppGi=0;
                    senarioOppBi=0;
                    for(int j=0;j<offers.size();j++){
                        if(offers.get(j).votedR == V[i]){if(offers.get(j).voteR>0) senarioRandomGi+=offers.get(j).voteR; else if(offers.get(j).voteR<0) senarioRandomBi+=offers.get(j).voteR;}
                        if(offers.get(j).votedP == V[i]){if(offers.get(j).voteP>0) senarioPGi+=offers.get(j).voteP; else if(offers.get(j).voteP<0) senarioPBi+=offers.get(j).voteP;}
                        if(offers.get(j).votedOpp == V[i]) {if(offers.get(j).voteOpp>0) senarioOppGi+=offers.get(j).voteOpp; else if(offers.get(j).voteOpp<0) senarioOppBi+=offers.get(j).voteOpp;}
                   
                    }
                    senarioRandomGi*=1/(double)wl;
                    senarioPGi*=1/(double)wl;
                    senarioOppGi*=1/(double)wl;

                    senarioRandomBi*=-1/(double)wl;
                    senarioPBi*=-1/(double)wl;
                    senarioOppBi*=-1/(double)wl;
                    
                    pPrimeR[i]=(double)(K[i]+etha*senarioRandomGi)/(DATA_SIZE+etha*(senarioRandomGi+senarioRandomBi));
                    pPrimeP[i]=(double)(K[i]+etha*senarioPGi)/(DATA_SIZE+etha*(senarioPGi+senarioPBi));
                    pPrimeO[i]=(double)(K[i]+etha*senarioOppGi)/(DATA_SIZE+etha*(senarioOppGi+senarioOppBi));
                                     //  System.out.println("crediblity of x["+i+"]= "+sampleData[i]+" is: "+UxOfSamples[i]);
                    sigmaPPrimeR+=pPrimeR[i];
                    sigmaPPrimeP+=pPrimeP[i];
                    sigmaPPrimeO+=pPrimeO[i];
                    
               }
               
               for(int i=0;i<numberOfBins;i++){
                    pPrimeR[i]=(double)pPrimeR[i]/sigmaPPrimeR;
                    pPrimeP[i]=(double)pPrimeP[i]/sigmaPPrimeP;
                    pPrimeO[i]=(double)pPrimeO[i]/sigmaPPrimeO;
                    out.println(V[i]+","+P[i]+","+pPrimeR[i]+","+pPrimeP[i]+","+pPrimeO[i]);

               }

               
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }             
            
            
            //update the reputations
            try(FileWriter fw = new FileWriter(RFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
               
               
               for(int j=0;j<MV;j++){
                            double sigmaDeltaRep=0;
                            for(int i=0;i<M;i++){
                                sigmaDeltaRep+=UxOfSamples[i]*votes[i][j]*Math.exp(-1*R[sampleVoters[j]]);
                            }
                            if(sigmaDeltaRep<0) R[sampleVoters[j]]=0;
                            else if(sigmaDeltaRep>10) R[sampleVoters[j]]=10; 
                            else R[sampleVoters[j]]=sigmaDeltaRep;
                        //    System.out.println("User["+sampleVoters[j]+"] updated reputation is "+R[sampleVoters[j]]);

                   }

               
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }             
                        
            
            
        /*
            
            
              //generate the Likelyhoods 
            try(FileWriter fw = new FileWriter(LFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                for(int i=0;i<DATA_SIZE;i++){
                    L[i]=1-Math.exp(-1*Math.pow(X[i]-mean, 2)/(2*Math.pow(dev, 2)))+epsilon;
    //                System.out.println("L["+i+"] is: "+L[i]);
                    out.println(X[i]+","+L[i]);
                }
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            
            
            //generate the Probabilities 
            try(FileWriter fw = new FileWriter(PFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                double total=0;
                for(int i=0;i<DATA_SIZE;i++){
                    total+=L[i];
                }
                
                for(int i=0;i<DATA_SIZE;i++){
                    pOld[i]=L[i]/total;
     //               System.out.println("pOld["+i+"] is: "+pOld[i]);
                    out.println(X[i]+","+pOld[i]);
                }                
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            
            //generate the samples
            try(FileWriter fw = new FileWriter(SFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {

                int C=0;
                double x;
                boolean[] visited= new boolean[DATA_SIZE];
                double[] lineSegments= new double[DATA_SIZE+1];

                lineSegments[0]=0;
                java.util.Arrays.fill(visited,0,DATA_SIZE-1,false);
                for(int i=0;i<DATA_SIZE;i++){
                    lineSegments[i+1]=pOld[i]+lineSegments[i];
                }
                while(C<M){
                    x=Math.random();
                    for(int i=0;i<DATA_SIZE-1;i++){
                        if(x>=lineSegments[i] && x<lineSegments[i+1]){
                            if(visited[i]) break; else {visited[i]=true; sampleData[C]=X[i]; C++; break;}
                        }
                    }
                    if(x>=lineSegments[DATA_SIZE-1]){
                            if(!visited[DATA_SIZE-1]) {visited[DATA_SIZE-1]=true; sampleData[C]=X[DATA_SIZE-1]; C++;}
                        }
                }
                
                for(int i=0;i<M;i++){
                    System.out.println("Sample["+i+"] is: "+sampleData[i]);

                }                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }       
            
            
            
            */

        String temp="gnuplot -e \"OUTPUT_VERSIONED='Xis_version"+version+".png'; INPUT_VERSIONED='Xis_version"+version+"'\""+" plotXis"; 
        PrintWriter printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        Process p=Runtime.getRuntime().exec("bash bashshit");
        p.waitFor();

        /*
        temp="gnuplot -e \"OUTPUT_VERSIONED='Lis_version"+version+".png'; INPUT_VERSIONED='Lis_version"+version+"'\""+" plotLis"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        p=Runtime.getRuntime().exec("bash bashshit");
        p.waitFor();
        
        temp="gnuplot -e \"OUTPUT_VERSIONED='Pis_version"+version+".png'; INPUT_VERSIONED='Pis_version"+version+"'\""+" plotPis"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        p=Runtime.getRuntime().exec("bash bashshit");        
        p.waitFor();
        
        temp="gnuplot -e \"OUTPUT_VERSIONED='users_version"+version+".png'; INPUT_VERSIONED='users_version"+version+"'\""+" plotUis"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        p=Runtime.getRuntime().exec("bash bashshit");  
        p.waitFor();
*/
        temp="gnuplot -e \"OUTPUT_VERSIONED='bins_version"+version+".png'; INPUT_VERSIONED='bins_version"+version+"'\""+" plotBins"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        p=Runtime.getRuntime().exec("bash bashshit");  
        p.waitFor();
        
        temp="gnuplot -e \"OUTPUT_VERSIONED='PPRIMEis_version"+version+".png'; INPUT_VERSIONED='PPRIMEis_version"+version+"'\""+" plotPPrimes"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        p=Runtime.getRuntime().exec("bash bashshit");  
        p.waitFor();        


          }
        


    }
    

