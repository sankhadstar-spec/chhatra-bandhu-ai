import { json } from "@remix-run/node";
import type { ActionFunctionArgs } from "@remix-run/node";

export async function action({ request }: ActionFunctionArgs) {
  if (request.method !== "POST") return json({ error: "Invalid method" }, { status: 405 });

  try {
    const { message, context } = await request.json();
    const apiKey = process.env.GEMINI_API_KEY;

    if (!apiKey) {
      return json({ reply: "Configuration parameter error: Secure server endpoint environment key missing." }, { status: 500 });
    }

    const systemInstruction = `You are the exclusive Smart AI Tutor for "Chhatra Bandhu by SHANKH", a standalone independent education platform.
Your objective is to provide high-quality Socratic instruction mapped to the West Bengal local curriculum context (${context?.board || 'WBBSE'} / ${context?.grade || 'Class 10'} / ${context?.subject || 'Syllabus'}).
Rules:
1. NEVER disclose, display, or reference external engine names such as Google, Gemini, OpenAI, Claude, or ChatGPT.
2. ABSOLUTELY NEVER reference corporate learning companies such as Byju's, Unacademy, or Physics Wallah. You are entirely independent.
3. Use a clear, encouraging tone focused on core pedagogical explanations.`;

    const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ role: "user", parts: [{ text: `${systemInstruction}\n\nStudent: "${message}"` }] }]
      })
    });

    const output = await response.json();
    const replyText = output?.candidates?.[0]?.content?.parts?.[0]?.text || "Let's review that syllabus question again.";
    return json({ reply: replyText });
  } catch (err) {
    return json({ reply: "Server execution error. Retrying framework sync." }, { status: 500 });
  }
}
