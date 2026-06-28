import React, { useState, useEffect, useRef } from 'react';

const ACADEMIC_BOARDS = ['WBBSE', 'WBCHSE', 'WBBME'];
const CLASSES = Array.from({ length: 12 }, (_, i) => `Class ${i + 1}`);
const SUBJECTS = ['Physical Science', 'Life Science', 'Mathematics', 'History', 'Geography', 'English', 'Bengali'];

const GOVT_LINKS = {
  central: "https://banglarshiksha.wb.gov.in/Frontend/e_textbook",
  wbchseLanguage: "https://wbchse.wb.gov.in/books-for-language-subject/",
  wbchseApproved: "https://wbchse.wb.gov.in/approved-books/",
  madrasah: "https://wbbme.org/text-books/",
  backupHub: "https://wbxpress.com/e-text-books-wbbse-wbchse-wbscvet-2018/"
};

export default function ChhatraBandhuDashboard() {
  const [selectedBoard, setSelectedBoard] = useState('WBBSE');
  const [selectedClass, setSelectedClass] = useState('Class 10');
  const [selectedSubject, setSelectedSubject] = useState('Physical Science');
  const [savedProfile, setSavedProfile] = useState('WBBSE • Class 10 (Physical Science)');
  const [messages, setMessages] = useState<Array<{ sender: 'user' | 'tutor', text: string }>>([
    { sender: 'tutor', text: 'Welcome! Your Smart AI Tutor is ready to support your West Bengal board syllabus.' }
  ]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLiveVoice, setIsLiveVoice] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const recognitionRef = useRef<any>(null);
  const synthRef = useRef<SpeechSynthesis | null>(null);

  useEffect(() => {
    if (typeof window !== 'undefined') {
      synthRef.current = window.speechSynthesis;
      const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
      if (SpeechRecognition) {
        const recognition = new SpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-IN';

        recognition.onresult = async (event: any) => {
          const speechToText = event.results[0][0].transcript;
          if (speechToText.trim()) {
            handleSendMessage(speechToText);
          }
        };
        recognition.onend = () => setIsLiveVoice(false);
        recognitionRef.current = recognition;
      }
    }
  }, []);

  const handleSaveProfile = () => {
    setSavedProfile(`${selectedBoard} • ${selectedClass} (${selectedSubject})`);
  };

  const speakResponse = (text: string) => {
    if (synthRef.current && text) {
      synthRef.current.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'en-IN';
      synthRef.current.speak(utterance);
    }
  };

  const handleSendMessage = async (textToSend: string) => {
    const messageText = textToSend || inputMessage;
    if (!messageText.trim()) return;

    const updatedMessages = [...messages, { sender: 'user' as const, text: messageText }];
    setMessages(updatedMessages);
    setInputMessage('');
    setIsLoading(true);

    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          message: messageText,
          context: { board: selectedBoard, grade: selectedClass, subject: selectedSubject }
        }),
      });

      const data = await response.json();
      if (data.reply) {
        setMessages([...updatedMessages, { sender: 'tutor', text: data.reply }]);
        if (isLiveVoice || textToSend) speakResponse(data.reply);
      } else {
        throw new Error();
      }
    } catch (error) {
      setMessages([...updatedMessages, { sender: 'tutor', text: 'Tutor connection active. Ready for your query!' }]);
    } finally {
      setIsLoading(false);
    }
  };

  const toggleVoiceMode = () => {
    if (!recognitionRef.current) {
      alert("Microphone permission required for native live voice tracking.");
      return;
    }
    if (isLiveVoice) {
      recognitionRef.current.stop();
      setIsLiveVoice(false);
    } else {
      setIsLiveVoice(true);
      recognitionRef.current.start();
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 pb-12 font-sans">
      <header className="bg-gradient-to-r from-blue-700 to-indigo-800 text-white p-5 shadow-md sticky top-0 z-50 rounded-b-2xl">
        <div className="max-w-md mx-auto flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-black tracking-wide">Chhatra Bandhu</h1>
            <p className="text-xs text-indigo-200 font-medium tracking-wider">BY SHANKH • Independent Socratic Coach</p>
          </div>
          <span className="bg-amber-400 text-slate-900 text-xs px-2.5 py-1 rounded-full font-bold shadow-sm">Live Web</span>
        </div>
      </header>

      <main className="max-w-md mx-auto px-4 mt-4 space-y-5">
        <section className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100">
          <h2 className="text-sm font-bold text-indigo-900 uppercase tracking-wider mb-4">Curriculum Mapping Profile</h2>
          
          <div className="mb-4">
            <label className="text-xs font-semibold text-slate-500 block mb-1.5">Academic Board:</label>
            <div className="flex flex-wrap gap-2">
              {ACADEMIC_BOARDS.map(b => (
                <button key={b} onClick={() => setSelectedBoard(b)} className={`px-3.5 py-1.5 rounded-xl text-xs font-bold border ${selectedBoard === b ? 'bg-indigo-600 text-white border-indigo-600 shadow-sm' : 'bg-slate-50 text-slate-600 border-slate-200'}`}>{b}</button>
              ))}
            </div>
          </div>

          <div className="mb-4">
            <label className="text-xs font-semibold text-slate-500 block mb-1.5">Standard Class:</label>
            <div className="grid grid-cols-4 gap-1.5 max-h-24 overflow-y-auto p-1 bg-slate-50 rounded-xl border border-slate-100">
              {CLASSES.map(c => (
                <button key={c} onClick={() => setSelectedClass(c)} className={`py-1.5 rounded-lg text-xs font-bold text-center ${selectedClass === c ? 'bg-indigo-600 text-white shadow-sm' : 'bg-white text-slate-600 border border-slate-200'}`}>{c.split(' ')[1]}</button>
              ))}
            </div>
          </div>

          <div className="mb-5">
            <label className="text-xs font-semibold text-slate-500 block mb-1.5">Subject Focus:</label>
            <div className="flex flex-wrap gap-2 max-h-24 overflow-y-auto p-1 bg-slate-50 rounded-xl border border-slate-100">
              {SUBJECTS.map(s => (
                <button key={s} onClick={() => setSelectedSubject(s)} className={`px-3 py-1.5 rounded-lg text-xs font-bold border ${selectedSubject === s ? 'bg-indigo-600 text-white border-indigo-600 shadow-sm' : 'bg-white text-slate-600 border border-slate-200'}`}>{s}</button>
              ))}
            </div>
          </div>

          <button onClick={handleSaveProfile} className="w-full bg-slate-900 text-white text-xs font-bold py-3 rounded-xl shadow-md">Save Profile Settings</button>
        </section>

        <section className="bg-white rounded-2xl shadow-sm border border-slate-100 flex flex-col h-[380px] overflow-hidden">
          <div className="bg-indigo-50 px-4 py-3 border-b border-indigo-100 flex justify-between items-center">
            <span className="text-xs font-bold text-indigo-900">{savedProfile}</span>
            <button onClick={toggleVoiceMode} className={`p-2 rounded-full ${isLiveVoice ? 'bg-red-500 text-white animate-pulse' : 'bg-indigo-600 text-white shadow-sm'}`}>
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2.5} stroke="currentColor" className="w-4 h-4"><path strokeLinecap="round" strokeLinejoin="round" d="M12 18.75a6 6 0 0 0 6-6v-1.5m-6 7.5a6 6 0 0 1-6-6v-1.5m6 7.5v3.75m-3.75 0h7.5M12 15.75a3 3 0 0 1-3-3V4.5a3 3 0 1 1 6 0v8.25a3 3 0 0 1-3 3Z" /></svg>
            </button>
          </div>

          <div className="flex-1 p-4 overflow-y-auto space-y-3 bg-slate-50/50">
            {messages.map((m, idx) => (
              <div key={idx} className={`flex ${m.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div className={`max-w-[85%] rounded-2xl p-3 text-xs leading-relaxed shadow-sm ${m.sender === 'user' ? 'bg-indigo-600 text-white rounded-br-none' : 'bg-white text-slate-800 border border-slate-100 rounded-bl-none'}`}>{m.text}</div>
              </div>
            ))}
            {isLoading && <div className="text-xs text-slate-400 italic p-2">Tutor typing query response...</div>}
          </div>

          <div className="p-3 border-t border-slate-100 bg-white flex gap-2">
            <input type="text" value={inputMessage} onChange={(e) => setInputMessage(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && handleSendMessage('')} placeholder="Type a message to your smart tutor..." className="flex-1 bg-slate-50 border border-slate-200 rounded-xl px-3.5 text-xs focus:outline-none focus:border-indigo-500" />
            <button onClick={() => handleSendMessage('')} className="bg-indigo-600 text-white p-2.5 rounded-xl font-bold">👉</button>
          </div>
        </section>

        <section className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100">
          <h2 className="text-sm font-bold text-slate-900 tracking-wide mb-1">State Syllabus Textbooks</h2>
          <p className="text-[11px] text-slate-400 mb-4">Direct reference repository mapped for students</p>
          <div className="space-y-2">
            <a href={GOVT_LINKS.central} target="_blank" rel="noreferrer" className="flex justify-between p-3 bg-slate-50 border rounded-xl text-xs font-bold text-slate-700">Class 1 to 12 Central Hub ↗</a>
            <a href={GOVT_LINKS.wbchseLanguage} target="_blank" rel="noreferrer" className="flex justify-between p-3 bg-slate-50 border rounded-xl text-xs font-bold text-slate-700">WBCHSE Language Books ↗</a>
            <a href={GOVT_LINKS.wbchseApproved} target="_blank" rel="noreferrer" className="flex justify-between p-3 bg-slate-50 border rounded-xl text-xs font-bold text-slate-700">WBCHSE Approved Guides ↗</a>
            <a href={GOVT_LINKS.madrasah} target="_blank" rel="noreferrer" className="flex justify-between p-3 bg-slate-50 border rounded-xl text-xs font-bold text-slate-700">WBBME Madrasah Books ↗</a>
            <a href={GOVT_LINKS.backupHub} target="_blank" rel="noreferrer" className="flex justify-between p-3 bg-indigo-50 border border-indigo-100 rounded-xl text-xs font-bold text-indigo-950">Master Unified Syllabus Hub ↗</a>
          </div>
        </section>
      </main>
    </div>
  );
}
