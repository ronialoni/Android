package il.ac.tau.team3.common;

//import il.ac.tau.team3.datastore.PlaceLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GeneralPlace extends GeneralLocation implements Serializable{

        /**
         * 
         */
        private static final long serialVersionUID = 3680632953183211194L;
        private String address;
        private GeneralUser owner;

        private List<Pray> praysOfTheDay;

        private Date startDate;
        private Date endDate;




        private void initializePraysOfTheDay()  {
                this.praysOfTheDay = new ArrayList<Pray>(3);
                this.praysOfTheDay.add(null);
                this.praysOfTheDay.add(null);
                this.praysOfTheDay.add(null);
        }

        public GeneralPlace(){
                super();
                initializePraysOfTheDay();
                this.startDate = new Date();
                this.endDate = new Date();
        }

        public GeneralPlace(String name, String address , SPGeoPoint spGeoPoint){
                super(spGeoPoint,name);
                this.address = address;
                initializePraysOfTheDay();
                this.startDate = new Date();
                this.endDate = new Date();
        }

        public GeneralPlace(GeneralUser owner, String name, String address , SPGeoPoint spGeoPoint, Date startDate,Date endDate){
                super(spGeoPoint,name);
                this.address = address;
                initializePraysOfTheDay();
                this.owner = owner;
                this.startDate = startDate;
                this.endDate = endDate;
        }
//        public GeneralPlace(PlaceLocation serverPlace){
//                this(serverPlace.getOwner(),serverPlace.getName(),serverPlace.getAddress(),
//                                new SPGeoPoint((int)(serverPlace.getLatitude()*1000000), (int)(serverPlace.getLongitude()*1000000)),serverPlace.getStartDate(),  serverPlace.getEndDate());
//                this.prays = serverPlace.getPrays();
//                if(null != serverPlace.getPraysOfTheDay()){
//                        for(int i = 0 ; i < 3 ; ++i){
//                                this.praysOfTheDay[i] = serverPlace.getPraysOfTheDay().get(i);
//                        }
//                }
//
//
//        }

        public List<Pray> getPraysOfTheDay() {
                return praysOfTheDay;
        }

        public void setPraysOfTheDay(List<Pray> praysOfTheDay) {
                this.praysOfTheDay = praysOfTheDay;
        }

        @JsonIgnore
        public void setPraysOfTheDay(int prayNumber, Pray praysOfTheDay) {
                try     {
                        this.praysOfTheDay.set(prayNumber, praysOfTheDay);
                } catch (IndexOutOfBoundsException e){
                        
                }
                
                
        }
        
        @JsonIgnore
        public void setPraysOfTheDay(String prayName, Pray praysOfTheDay) {
                for (int i = 0; i < this.praysOfTheDay.size(); i++)     {
                        if ( this.praysOfTheDay.get(i).getName().equals(praysOfTheDay.getName()))       {
                                this.praysOfTheDay.set(i, praysOfTheDay);
                        }
                }
                
        }
        
        @JsonIgnore
        public Pray getPrayByName(String prayName)      {
                for (int i = 0; i < this.praysOfTheDay.size(); i++)     {
                        if ( this.praysOfTheDay.get(i).getName().equals(prayName))      {
                                return this.praysOfTheDay.get(i); 
                        }
                }
                
                return null;
                
        }
        
        


        public Date getStartDate() {
                return startDate;
        }

        public void setStartDate(Date startDate) {
                this.startDate = startDate;
        }

        public Date getEndDate() {
                return endDate;
        }

        public void setEndDate(Date endDate) {
                this.endDate = endDate;
        }


        public GeneralUser getOwner() {
                return owner;
        }

        public void setOwner(GeneralUser owner) {
                this.owner = owner;
        }




        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }



        @JsonIgnore
        public boolean IsJoinerSigned(int prayNumber, GeneralUser joiner){
                try     {
                        return (this.praysOfTheDay.get(prayNumber).isJoinerSigned(joiner));
                } catch (IndexOutOfBoundsException e)   {       
                        return false;
                } catch (NullPointerException e)        {
                        return false;
                }
                
        }

        @JsonIgnore  
        public int getNumberOfPrayers(int prayNumber){
                try     {
                return this.praysOfTheDay.get(prayNumber).numberOfJoiners();
                }  catch (IndexOutOfBoundsException e)  {       
                return 0;
            } catch (NullPointerException e)    {
                return 0;
            }
        }


}