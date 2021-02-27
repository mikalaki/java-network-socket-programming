
% Function that gets the system response times array and returns the throughput
% for each second ( for instance ThroughputArray(i), gives the throughput
% for i-th second).
function ThroughputArray = getThroughputPerSec(resTimesArray)

SecondsSum=0;
ThroughputArray=[];
packetsCounter=0;

for i = 1 : length(resTimesArray)
    SecondsSum = SecondsSum + resTimesArray(i); 
    
    if(SecondsSum < 1000)
        packetsCounter = packetsCounter+1;
        
        if i == length(resTimesArray)
            %If we are in the last packet we add the counted packets in the last
             %second 
            ThroughputArray=[ThroughputArray packetsCounter];
        end
        
    else
        while(SecondsSum >= 1000)
           ThroughputArray=[ThroughputArray packetsCounter];
           packetsCounter=0;
           SecondsSum = SecondsSum- 1000;
           
           if(SecondsSum < 1000)
                packetsCounter = packetsCounter+1;
                if i == length(resTimesArray)
                %If we are in the last packet we add the counted packets in the last
                 %second 
                ThroughputArray=[ThroughputArray packetsCounter];
                end
           end
           
        end        
    end
    
end


end

