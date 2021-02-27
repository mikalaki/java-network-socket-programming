%% Statistical Analysis for the Computer Networks 2 project
%  Java Socket Programming data statistical analysis.


%% Importing Data
echo_responseTimes_delay= importdata('echoPacketsResTimes_delay.csv');
echo_responseTimes_noDelay= importdata('echoPacketsResTimes_NoDelay.csv');

%% Part [B] of the assigment 
% plot G1
figure()
plot(echo_responseTimes_delay)
title("G1:EchoPackets with delay response times - 29/11/2020 00:20:33-00:24:48 - E5249- IP:87.202.49.46")
ylabel("response time in milliseconds")
xlabel("n. of packet")
meanResTime_echodelay = mean(echo_responseTimes_delay);
varResTime_echodelay = var(echo_responseTimes_delay);
fprintf("G1:Mean Value of response time for echo packets with delay is: %f \n",meanResTime_echodelay);
fprintf("G1:Variance of response time for echo packets with delay is: %f\n",varResTime_echodelay);
% string1 = sprintf("mean value = %f",meanResTime_echodelay)
% string2 = sprintf("Variance = %f",varResTime_echodelay)
% legend(string1,string2)
fprintf("\n");


% plot G2
% Get the Throughput for each second of echo packets interchange process.
ThroughputWithDelay = getThroughputPerSec(echo_responseTimes_delay);
%echo packet length is 32 bytes and 1 byte = 8 bits
throughputIn_bps = ThroughputWithDelay * 32 * 8;
windowWidth = 8; 
% kernel = ones(windowWidth,1) / windowWidth;
% throughputIn_bps_MVA = filter(kernel, 1, throughputIn_bps);
B = 1/windowWidth*ones(windowWidth,1);
throughputIn_bps_MVA = filter(B,1,throughputIn_bps);
figure();
plot(throughputIn_bps_MVA);
title("G2:EchoPackets Throughput with MA filter of 8 secs - 29/11/2020 00:20:33-00:24:48 - E5249- IP:87.202.49.46");
xlabel("n.of second");
ylabel("Throughput in bps (bits/second)");
meanThroughputWithMVA = mean(throughputIn_bps_MVA);
varThroughputWithMVA = var(throughputIn_bps_MVA);
fprintf("G2:Mean Value of throughput values with MVA filter is: %f \n",meanThroughputWithMVA);
fprintf("G2:Variance of throughput values with MVA filter is: %f\n",varThroughputWithMVA);
fprintf("\n");

% plot G3
figure()
plot(echo_responseTimes_noDelay)
title("G3:EchoPackets without delay response times - 29/11/2020 00:24:48-00:29:03 - E0000- IP:87.202.49.46")
ylabel("Response Time in milliseconds")
xlabel("n. of packet")
meanResTime_echoNodelay = mean(echo_responseTimes_noDelay);
varResTime_echoNodelay = var(echo_responseTimes_noDelay);
fprintf("G3:Mean Value of response time for echo packets without delay is: %f \n",meanResTime_echoNodelay);
fprintf("G3:Variance of response time for echo packets without delay is: %f\n",varResTime_echoNodelay);
% string1 = sprintf("mean value = %f",meanResTime_echodelay)
% string2 = sprintf("Variance = %f",varResTime_echodelay)
% legend(string1,string2)
fprintf("\n");


% plot G4
% Get the Throughput for each second of echo packets interchange process.
ThroughputNoDelay = getThroughputPerSec(echo_responseTimes_noDelay);
%echo packet length is 32 bytes and 1 byte = 8 bits
throughputIn_bps_noDelay = ThroughputNoDelay * 32 * 8;
windowWidth = 8;
kernel = ones(windowWidth,1) / windowWidth;
throughputIn_bps_MVA_noDelay = filter(kernel, 1, throughputIn_bps_noDelay);
figure();
plot(throughputIn_bps_MVA_noDelay);
title("G4:EchoPackets no delay Throughput with MA filter of 8 secs- 29/11/2020 00:24:48-00:29:03 - E0000- IP:87.202.49.46");
xlabel("n.of second");
ylabel("Throughput in bps (bits/second)");
meanThroughputWithMVA_noDelay = mean(throughputIn_bps_MVA_noDelay);
varThroughputWithMVA_noDelay = var(throughputIn_bps_MVA_noDelay);
fprintf("G4:Mean Value of throughput values with MVA filter is: %f \n",meanThroughputWithMVA_noDelay);
fprintf("G4:Variance of throughput values with MVA filter is: %f\n",varThroughputWithMVA_noDelay);


%G5 response times of echo packets with delay histogram - histogram of values of G1
figure();
histogram(echo_responseTimes_delay,15,'Normalization','probability');
title("G5: Histogram of response times of echo packets with delay - 29/11/2020 00:20:33-00:24:48 - E5249- IP:87.202.49.46");
ylabel("frequency");
xlabel("values");

%G6: Histogram of MA filtered throughput of echo packets with delay
figure();
histogram(throughputIn_bps_MVA,15,'Normalization','probability')
title("G6: Histogram MA filtered throughput of echo packets with delay - 29/11/2020 00:20:33-00:24:48 - E5249- IP:87.202.49.46")
ylabel("frequency");
xlabel("values");

%G7: response times of echo packets without delay histogram - histogram of values of G1
figure();
histogram(echo_responseTimes_noDelay,15,'Normalization','probability')
title("G7: Histogram of response times of echo packets without delay - 29/11/2020 00:24:48-00:29:03 - E0000- IP:87.202.49.46")
ylabel("frequency");
xlabel("values");

%G8: Histogram of MA filtered throughput of echo packets without delay
figure();
histogram(throughputIn_bps_MVA_noDelay,15,'Normalization','probability')
title("G8: Histogram MA filtered throughput of echo packets without delay - 29/11/2020 00:24:48-00:29:03 - E0000- IP:87.202.49.46")
ylabel("frequency");
xlabel("values");

%plot
%ThroughputNoDelay = getThroughputPerSec(echo_responseTimes_noDelay);



% R1 diagram.
RTT = echo_responseTimes_delay;
alpha = 0.9;
beta = 0.85;
gamma = 3.2;
SRTT = RTT;
for i = [1:length(echo_responseTimes_delay)-1]
   SRTT(i+1)= alpha * SRTT(i) + (1 - alpha) * RTT(i+1) ;
end

roundTripTimeVariance = abs(SRTT-RTT);
roundTripTimeVariance(1) =  abs(mean(RTT-SRTT));
for i = [1:length(echo_responseTimes_delay)-1]
   roundTripTimeVariance(i+1) = beta * roundTripTimeVariance(i) + (1-beta)* abs(SRTT(i+1)-RTT(i+1));
end

RTO = SRTT + gamma * roundTripTimeVariance;
figure();
plot(RTT);
hold on;
plot(SRTT);
hold on;
plot(roundTripTimeVariance);
hold on;
plot(RTO);
title("R1 diagram - RTT from delayed echo packets - 29/11/2020 00:20:33-00:24:48 - E5249- IP:87.202.49.46");
legend("RTT","SRTT","roundTripVariance","RTO");
xlabel("index");

% G 
% find the distribution of delay
echo_DelayDiffs = echo_responseTimes_delay - meanResTime_echoNodelay;
meanDelayEcho = mean(echo_DelayDiffs);
varDelayEcho = var(echo_DelayDiffs);
figure();
histfit(echo_DelayDiffs,12);
title("Histogram of echo Packets Delay - E5249 - IP:87.202.49.46");
ylabel("Absolute frequency");
xlabel("Values");
fprintf("\n Mean Value of Delays: %f \n",meanDelayEcho);
fprintf("Variance of Delays: %f\n",varDelayEcho);
% finding the frequency
% importing DPCM sound
FreqSamplesDPCM = importdata('DPCM_freq_actual_samples.csv');
songSamplesDPCM = importdata('DPCM_song_actual_samples.csv');
FreqSamplesDiffsDPCM = importdata('DPCM_freq_samples_diff.csv'); 
songSamplesDiffsDPCM = importdata('DPCM_song_samples_diff.csv');

Fs = 8000;            % Sampling frequency                    
T = 1/Fs;             % Sampling period       
L = 1000; %length(FreqSamplesDPCM);             % Length of signal
t = (0:L-1)*T;        % Time vector

Y = fft(FreqSamplesDPCM(1:1000));
P2 = abs(Y/L);
P1 = P2(1:L/2+1);
P1(2:end-1) = 2*P1(2:end-1);
f = Fs*(0:(L/2))/L;
figure();
plot(f,P1) ;
title('Single-Sided Amplitude Spectrum of our frequency sample - IP:87.202.49.46 ');
xlabel('f (Hz) - spike gives the frequency');
ylabel('|P1(f)|');

% F = 664 HZ

%D 
% D) 
% G9
figure();
plot(FreqSamplesDPCM(1:1500));
axis([0 1500 -20 20])
title("G9: First 1500 samples of the Frequency generator sample (DPCM) - 29/11/2020 00:30:22-00:31:13 - A8588 - IP:87.202.49.46");
xlabel("n.of Sample");
ylabel("Amplitude");

% G10
figure();
plot(songSamplesDPCM(1:1500));
axis([0 1500 -20 20])
title("G10: First 1500 samples of the song(RADIOACTIVITY - KRAFTWERK) sample (DPCM)- 29/11/2020 00:31:22-00:32:13 - A8588 - IP:87.202.49.46");
xlabel("n.of Sample");
ylabel("Amplitude");


% G11 DPCM SONG - samples Different  HISTOGRAMM
figure();
histogram(songSamplesDiffsDPCM,15,'Normalization','probability')
title("G11:Histogram of Samples Differences - DPCM song(RADIOACTIVITY - KRAFTWERK) - 29/11/2020 00:31:22-00:32:13 - A8588 - IP:87.202.49.46")
ylabel("Absolute frequency");
xlabel("values");

% G12 DPCM SONG - samples HISTOGRAMM
figure();
histogram(songSamplesDPCM,15,'Normalization','probability')
title("G12:Histogram of Actual Samples - DPCM song(RADIOACTIVITY - KRAFTWERK) - 29/11/2020 00:31:22-00:32:13 - A8588 - IP:87.202.49.46")
ylabel("Absolute frequency");
xlabel("values");

%importing AQDPCM songs
song1SamplesAQDPCM = importdata('AQDPCM_song1_actual_samples.csv');
song1SamplesDiffsAQDPCM = importdata('AQDPCM_song1_samples_diff.csv');
song1MeanValuesAQDPCM = importdata('AQDPCM_song1_meanValues.csv');
song1StepsAQDPCM = importdata('AQDPCM_song1_steps.csv');

song2SamplesAQDPCM = importdata('AQDPCM_song2_actual_samples.csv');
song2SamplesDiffsAQDPCM = importdata('AQDPCM_song2_samples_diff.csv');
song2MeanValuesAQDPCM = importdata('AQDPCM_song2_meanValues.csv');
song2StepsAQDPCM = importdata('AQDPCM_song2_steps.csv');

% G13 AQDPCM SONG 1 - samples Different HISTOGRAMM
figure();
histogram(song1SamplesDiffsAQDPCM,15,'Normalization','probability')
title("G13: Histogram of Samples Differences - AQDPCM song1(Symphony No. 9: Ode To Joy) - 29/11/2020 00:32:23-00:33:01 - A8588 - IP:87.202.49.46")
ylabel("Absolute frequency");
xlabel("values");

% G14 AQDPCM SONG 1 - samples HISTOGRAMM
figure();
histogram(song1SamplesAQDPCM,15,'Normalization','probability')
title("G14: Histogram of Actual Samples - AQDPCM song1(Symphony No. 9: Ode To Joy) - 29/11/2020 00:32:23-00:33:01 - A8588 - IP:87.202.49.46")
ylabel("Absolute frequency");
xlabel("values");

% G15 AQDPCM SONG 1 - mean values
figure();
plot(song1MeanValuesAQDPCM(1:400));
title("G15: Mean Value of AQDPCM quantiazer -  song1(Symphony No. 9: Ode To Joy) - 29/11/2020 00:32:23-00:33:01 - A8588 - IP:87.202.49.46")
ylabel("Mean Value of Quantizer");
xlabel("Index of Mean Value");

% G16 AQDPCM SONG 1 - quantizer
figure();
plot(song1StepsAQDPCM(1:400));
title("G16: Step of AQDPCM quantiazer -  song1(Symphony No. 9: Ode To Joy) - 29/11/2020 00:32:23-00:33:01 - A8588 - IP:87.202.49.46")
ylabel("Step value of Quantizer");
xlabel("Index Step value");


% G17 AQDPCM SONG 2 - mean values
figure();
plot(song2MeanValuesAQDPCM(1:400));
title("G17: Mean Value of AQDPCM quantiazer -  song2(Den boro na perimeno - Nikites) - 29/11/2020 00:33:09-00:33:47 - A8588 - IP:87.202.49.46")
ylabel("Mean Value of Quantizer");
xlabel("Index of Mean Value");

% G18 AQDPCM SONG 2 - quantizer
figure();
plot(song2StepsAQDPCM(1:400));
title("G18: Step of AQDPCM quantiazer -  song2(Den boro na perimeno - Nikites) - 29/11/2020 00:33:09-00:33:47 - A8588 - IP:87.202.49.46")
ylabel("Step value of Quantizer");
xlabel("Index Step value");

% ITHAKI COPTER
copterData1 = importdata('Telemetry_LLL_RRR_AAA_TTTT_PPPPPP_1.csv');
copterData2 = importdata('Telemetry_LLL_RRR_AAA_TTTT_PPPPPP_2.csv');

%G19: Copter Telemetry from 1st ithaki copter data download
figure()
plot(copterData1(:,1),"-o");
hold on;
plot(copterData1(:,2));
hold on;
plot(copterData1(:,3));
title("G19: Ithaki Copter 1st Telemetry Data - 29/11/2020 00:34:00 - 00:34:39 - Q2593(not-used) - IP:87.202.49.46");
ylabel("values");
xlabel("n. of Response");

legend("left motor PWM duty-cycle","right-motor PWM duty-cycle","Altitude");

%G20: Copter Telemetry from 2nd ithaki copter data download
figure()
plot(copterData2(:,1),"-o");
hold on;
plot(copterData2(:,2));
hold on;
plot(copterData2(:,3));
title("G20: Ithaki Copter 2nd Telemetry Data - 29/11/2020 00:34:48 - 00:35:27- Q2593(not-used) - IP:87.202.49.46");
ylabel("values");
xlabel("n. of Response");

legend("left motor PWM duty-cycle","right-motor PWM duty-cycle","Altitude");

% ODB-II data 
OBD_vehicle_Speed = importdata('OBD_VehicleSpeed.csv');
OBD_ThrottlePosition = importdata('OBD_ThrottlePosition.csv');
OBD_IntakeAirTemperature = importdata('OBD_IntakeAirTemperature.csv');
OBD_engineRunTimes = importdata('OBD_engineRunTimes.csv');
OBD_EngineRPM = importdata('OBD_EngineRPM.csv');
OBD_CoolantTemperature = importdata('OBD_CoolantTemperature.csv');

figure()
plot(OBD_engineRunTimes);
title("OBD-II vehicle - Engine Run Time - 29/11/2020 00:35:39 - 00:39:39 - V5145 - IP:87.202.49.46");
ylabel("Engine Run Time (seconds)")
xlabel("n. of request");


figure()
plot(OBD_IntakeAirTemperature);
title("OBD-II vehicle - Intake Air Temperature - 29/11/2020 00:35:39 - 00:39:39 - V5145 - IP:87.202.49.46");
ylabel("Intake Air Temperature (^oC)")
xlabel("n. of request");

figure()
plot(OBD_ThrottlePosition);
title("OBD-II vehicle - Throttle Position - 29/11/2020 00:35:39 - 00:39:39 - V5145 - IP:87.202.49.46");
ylabel("value")
xlabel("Throttle Position (%)");

figure()
plot(OBD_EngineRPM);
title("OBD-II vehicle - Engine RPM - 29/11/2020 00:35:39 - 00:39:39 - V5145 - IP:87.202.49.46");
ylabel("Engine RPM")
xlabel("n. of request");

figure()
plot(OBD_vehicle_Speed);
title("OBD-II vehicle - Vehicle Speed - 29/11/2020 00:35:39 - 00:39:39 - V5145 - IP:87.202.49.46");
ylabel("Vehicle Speed (Km/h)")
xlabel("n. of request");

figure()
plot(OBD_CoolantTemperature);
title("OBD-II vehicle - Coolant Temperature - 29/11/2020 00:35:39 - 00:39:39 - V5145 - IP:87.202.49.46");
ylabel("Coolant Temperature (^oC)")
xlabel("n. of request");








