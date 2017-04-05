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
    private static final String VFILENAME = "votes";
    private static final String UXFILENAME = "Uxis";
    private static final String RFILENAME = "Rjs";
    
    

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("Hello World!"); // Display the string.
        long version=System.currentTimeMillis();
        int maximum=100;
        int minimum=1;
        int randomNum=0;
        int DATA_SIZE=1000;
        int USER_SIZE=1000;
        double mean=0;
        double dev=1;
        
        double mean2=0;
        double dev2=1;    
        
        double[] X= new double[DATA_SIZE];
        double[] Ux= new double[DATA_SIZE];   
        double[] L= new double[DATA_SIZE];
        double[] P= new double[DATA_SIZE];
        double epsilon=0;
        double sigma=0.25;
        double l=1;

        double[] U= new double[USER_SIZE];
        double[] Q= new double[USER_SIZE];
        double[] NQ= new double[USER_SIZE];
        double[] T= new double[USER_SIZE];
        double[] R= new double[USER_SIZE];
        
        
        
        int M=100;
        int MV=100;  
        double[] sampleData= new double[M];
        double[] UxOfSamples= new double[M]; 
        int[] sampleVoters= new int[MV];
        int[][] votes= new int[M][MV];
        
        

        
        randomNum = minimum + (int)(Math.random() * maximum);
        System.out.println(randomNum); // Display the string.
        
        //generate the transactions
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
            
            
            
            //generate the Likelyhoods
            try(FileWriter fw = new FileWriter(LFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                for(int i=0;i<DATA_SIZE;i++){
                    L[i]=1-Math.exp(-1*Math.pow(X[i]-mean, 2)/(2*Math.pow(dev, 2)))+epsilon;
                    System.out.println("L["+i+"] is: "+L[i]);
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
                    P[i]=L[i]/total;
                    System.out.println("P["+i+"] is: "+P[i]);
                    out.println(X[i]+","+P[i]);
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
                    lineSegments[i+1]=P[i]+lineSegments[i];
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
            
            
        //generate the users 
            try(FileWriter fw = new FileWriter(UFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {

                for(int i=0;i<USER_SIZE;i++){
                    Random r = new Random();
                    U[i]=r.nextGaussian()*dev2+mean2;
                    System.out.println("U["+i+"] is: "+U[i]);
                    out.println(i+","+U[i]);
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
                    Q[j]=(2/(1+Math.exp(-0.01*(now-T[j])*(R[j]+sigma))))-1;
                    System.out.println("Q["+j+"] is: "+Q[j]);
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
                    System.out.println("NQ["+j+"] is: "+NQ[j]);
                    out.println(j+","+NQ[j]);
                }                
                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }            
            
            
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
                    System.out.println("Sample["+i+"] is: "+sampleVoters[i]);

                }                
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }            
                 
            
            //generate the votes
            try(FileWriter fw = new FileWriter(VFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
               double maliciousity=dev2;
               double ignorranceDegree=0;
               for(int i=0;i<M;i++){
                   if(abs(sampleData[i])<= dev){
                            for(int j=0;j<MV;j++){
                                if(U[sampleVoters[j]]> maliciousity) votes[i][j]=-1; else { if(Math.random()>ignorranceDegree) votes[i][j]=1; else votes[i][j]=0; }
                                System.out.println("User["+j+"] voted "+votes[i][j]+" for sampleData "+sampleData[i]);
                            }
                   }
                   else {
                            for(int j=0;j<MV;j++){
                                if(U[sampleVoters[j]]> maliciousity) votes[i][j]=1; else { if(Math.random()>ignorranceDegree) votes[i][j]=-1; else votes[i][j]=0; }
                                System.out.println("User["+j+"] voted "+votes[i][j]+" for sampleData "+sampleData[i]);
                            }
                   }                   
                   
               }
                
              
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }             
            
            
            //generate the crediblities
            try(FileWriter fw = new FileWriter(UXFILENAME+"_version"+version, false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
               double Gx=0;
               double Bx=0;
               for(int i=0;i<M;i++){
                    Gx=0;
                    Bx=0;
                    for(int j=0;j<MV;j++){
                        if(votes[i][j]==1) Gx++; else if(votes[i][j]==-1) Bx++;
                    }
                    UxOfSamples[i]=(Gx-Bx)/(Gx+Bx+1);
                                     //  System.out.println("crediblity of x["+i+"]= "+sampleData[i]+" is: "+UxOfSamples[i]);

               }
               
               for(int i=0;i<DATA_SIZE;i++){
                   int ni=0;
                   double minAbsDiffxini=abs(X[i]-sampleData[0]);
                   for(int j=0;j<M;j++){
                       if(abs(X[i]-sampleData[j])<minAbsDiffxini) {ni=j; minAbsDiffxini=abs(X[i]-sampleData[j]);}                       
                   }
                   Ux[i]=UxOfSamples[ni]*Math.exp(-1*l*minAbsDiffxini);
                   System.out.println("crediblity of x["+i+"]= "+X[i]+" is: "+Ux[i]);

                   
                   
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
                            System.out.println("User["+sampleVoters[j]+"] updated reputation is "+R[sampleVoters[j]]);

                   }

               
                
                if(bw!=null)
                   bw.close();
                if(fw!=null)
                   fw.close();

            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }             
                        
            
            
            
       /*     

        String temp="gnuplot -e \"OUTPUT_VERSIONED='Xis_version"+version+".png'; INPUT_VERSIONED='Xis_version"+version+"'\""+" plotXis"; 
        PrintWriter printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        Runtime.getRuntime().exec("bash bashshit");
        
        temp="gnuplot -e \"OUTPUT_VERSIONED='Lis_version"+version+".png'; INPUT_VERSIONED='Lis_version"+version+"'\""+" plotLis"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        Runtime.getRuntime().exec("bash bashshit");
        
        temp="gnuplot -e \"OUTPUT_VERSIONED='Pis_version"+version+".png'; INPUT_VERSIONED='Pis_version"+version+"'\""+" plotPis"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        Runtime.getRuntime().exec("bash bashshit");        
        
        temp="gnuplot -e \"OUTPUT_VERSIONED='users_version"+version+".png'; INPUT_VERSIONED='users_version"+version+"'\""+" plotUis"; 
        printWriter = new PrintWriter ("bashshit");
        printWriter.println (temp);
        printWriter.close (); 
        System.out.println(temp);
        Runtime.getRuntime().exec("bash bashshit");  

*/

          }
        


    }
    

