package report.days.myapplication;

public class Data {
    private String WorkName;
    private int TheTime,EndTime;
    private int Count;
    private Data NextData;

    public Data(String workName,int time,int count,Data nextData)
    {
        WorkName=workName;
        TheTime=time;
        Count=count;
        EndTime=-1;
        NextData=nextData;

    }

    public Data SortingData()
    {
        if(NextData!=null) {

            NextData = NextData.SortingData();

            if(NextData.getTheTime()<TheTime)
            {
                Data swap=NextData.getNextData();
                Data ret=NextData;
                NextData.setNextData(this);

                NextData=swap;

                return ret;



            }




        }
        return this;
    }
    public void Erase()
    {
        if(NextData!=null)NextData.Erase();
        NextData=null;
    }
    public void AddTimeAndCount(int time,int count)
    {
        TheTime+=time;
        Count+=count;

    }

    public void setNextData(Data val){NextData=val;}
    public Data getNextData(){return  NextData;}
    public  int getTheTime(){return TheTime;}
    public int getCount(){return Count;}
    public String getWorkName(){return WorkName;}
    public int getEndTime(){return EndTime;}
    public void setEndTime(int val){EndTime=val;}

}
