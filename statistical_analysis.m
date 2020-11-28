%% Statistical Analysis for the Computer Networks 2 project
%  Java Socket Programming data statistical analysis.


%% Importing Data
echo_responeTimes_delay= importdata('echoPacketsResTimes_delay.csv');
echo_responeTimes_noDelay= importdata('echoPacketsResTimes_NoDelay.csv');

%% Part [B] of the assigment 
% Get the Throughput for each second of echo packets interchange process.
ThroughputWithDelay = getThroughputPerSec(echo_responeTimes_delay);
ThroughputNoDelay = getThroughputPerSec(echo_responeTimes_noDelay);

%%%%%% meta theelei MA filtro kai eimaste ok. ta bytes enos mynhmatos einai
%%%%%% 32 gia ta echo packets

% R1 diagram.




%% G9- G10
FreqSamplesDPCM = importdata('DPCM_freq_actual_samples.csv');
songSamplesDPCM = importdata('DPCM_song_actual_samples.csv');

FreqSamplesDiffsDPCM = importdata('DPCM_freq_samples_diff.csv'); 
songSamplesDiffsDPCM = importdata('DPCM_song_samples_diff.csv');

Fs = 8000;            % Sampling frequency                    
T = 1/Fs;             % Sampling period       
L = 1000 %length(FreqSamplesDPCM);             % Length of signal
t = (0:L-1)*T;        % Time vector

Y = fft(FreqSamplesDPCM(1:1000));
P2 = abs(Y/L);
P1 = P2(1:L/2+1);
P1(2:end-1) = 2*P1(2:end-1);
f = Fs*(0:(L/2))/L;
figure();
plot(f,P1) 
title('Single-Sided Amplitude Spectrum of our frequency sample')
xlabel('f (Hz)')
ylabel('|P1(f)|')




